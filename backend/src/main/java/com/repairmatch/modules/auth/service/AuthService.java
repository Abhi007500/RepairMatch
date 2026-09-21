package com.repairmatch.modules.auth.service;

import com.repairmatch.common.exception.BadRequestException;
import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.common.security.JwtTokenProvider;
import com.repairmatch.modules.auth.domain.OtpVerification;
import com.repairmatch.modules.auth.dto.*;
import com.repairmatch.modules.auth.repository.OtpVerificationRepository;
import com.repairmatch.modules.auth.sms.SmsOtpProvider;
import com.repairmatch.modules.auth.sms.SmsProviderFactory;
import com.repairmatch.modules.auth.sms.SmsSendResult;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TechnicianProfileRepository technicianProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OtpVerificationRepository otpVerificationRepository;
    private final SmsProviderFactory smsProviderFactory;

    private final int otpExpirationMinutes;
    private final int otpCooldownSeconds;
    private final int otpMaxAttempts;
    private final boolean allowDemoFallback;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            TechnicianProfileRepository technicianProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            OtpVerificationRepository otpVerificationRepository,
            SmsProviderFactory smsProviderFactory,
            @Value("${app.otp.expiration-minutes:5}") int otpExpirationMinutes,
            @Value("${app.otp.cooldown-seconds:60}") int otpCooldownSeconds,
            @Value("${app.otp.max-attempts:5}") int otpMaxAttempts,
            @Value("${app.otp.allow-demo-fallback:true}") boolean allowDemoFallback) {
        this.userRepository = userRepository;
        this.technicianProfileRepository = technicianProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.otpVerificationRepository = otpVerificationRepository;
        this.smsProviderFactory = smsProviderFactory;
        this.otpExpirationMinutes = otpExpirationMinutes;
        this.otpCooldownSeconds = otpCooldownSeconds;
        this.otpMaxAttempts = otpMaxAttempts;
        this.allowDemoFallback = allowDemoFallback;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email address already exists.");
        }

        String userId = UUID.randomUUID().toString();
        User user = new User(
                userId,
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                request.getPhoneNumber(),
                request.getRole()
        );

        userRepository.save(user);

        // If technician, create baseline technician profile
        if ("TECHNICIAN".equalsIgnoreCase(request.getRole())) {
            TechnicianProfile profile = new TechnicianProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setUser(user);
            profile.setBio("Experienced technician ready for service.");
            profile.setExperienceYears(1);
            profile.setVerificationStatus("PENDING");
            profile.setBaseInspectionFee(new BigDecimal("199.00"));
            profile.setServiceRadiusKm(15.0);
            profile.setLatitude(12.9716);
            profile.setLongitude(77.5946);
            profile.setAvailable(true);
            technicianProfileRepository.save(profile);
        }

        String token = tokenProvider.generateTokenFromEmail(
                user.getEmail(),
                user.getId(),
                user.getRole(),
                user.getFullName()
        );

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = tokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserDto.fromEntity(user);
    }

    public String validateAndCleanIndianPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException("Mobile phone number is required.");
        }
        String clean = phone.replaceAll("[^0-9]", "");
        if (clean.length() > 10 && clean.startsWith("91")) {
            clean = clean.substring(2);
        } else if (clean.length() > 10 && clean.startsWith("0")) {
            clean = clean.substring(1);
        }

        if (clean.length() != 10 || !clean.matches("^[6-9]\\d{9}$")) {
            throw new BadRequestException("Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9.");
        }
        return clean;
    }

    private String generateSecureOtp() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    @Transactional
    public SendOtpResponse sendOtp(SendOtpRequest request) {
        String cleanPhone = validateAndCleanIndianPhone(request.getPhoneNumber());

        // Check for active verification and enforce cooldown
        Optional<OtpVerification> latestOpt = otpVerificationRepository.findLatestActive(cleanPhone);
        if (latestOpt.isPresent()) {
            OtpVerification active = latestOpt.get();
            if (LocalDateTime.now().isBefore(active.getResendAvailableAt())) {
                long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), active.getResendAvailableAt());
                throw new BadRequestException("Please wait " + Math.max(1, remainingSeconds) + " seconds before requesting another code.");
            }
            // Invalidate older session so only new OTP is valid
            active.setVerified(true);
            otpVerificationRepository.save(active);
        }

        // Generate cryptographically secure random 6-digit OTP
        String rawOtp = generateSecureOtp();
        String otpHash = passwordEncoder.encode(rawOtp);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(otpExpirationMinutes);
        LocalDateTime resendAvailableAt = now.plusSeconds(otpCooldownSeconds);

        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                cleanPhone,
                otpHash,
                otpMaxAttempts,
                expiresAt,
                resendAvailableAt
        );
        otpVerificationRepository.save(verification);

        // Dispatch via configured SMS provider
        SmsOtpProvider provider = smsProviderFactory.getProvider();
        SmsSendResult sendResult = provider.sendOtp(cleanPhone, rawOtp);

        String demoOtp = allowDemoFallback ? rawOtp : null;

        return new SendOtpResponse(
                true,
                sendResult.getMessage(),
                cleanPhone,
                otpCooldownSeconds,
                otpExpirationMinutes * 60,
                sendResult.isDispatched(),
                demoOtp
        );
    }

    @Transactional(noRollbackFor = BadRequestException.class)
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String cleanPhone = validateAndCleanIndianPhone(request.getPhoneNumber());

        OtpVerification verification = otpVerificationRepository.findLatestActive(cleanPhone)
                .orElseThrow(() -> new BadRequestException("No active verification code found for this number. Please request a new code."));

        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new BadRequestException("Verification code has expired. Please request a new code.");
        }

        if (verification.getAttempts() >= verification.getMaxAttempts()) {
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new code.");
        }

        String submittedOtp = request.getOtp() != null ? request.getOtp().trim() : "";
        boolean isMatch = passwordEncoder.matches(submittedOtp, verification.getOtpHash());

        // Allow demo fallback in local dev mode if enabled
        if (!isMatch && allowDemoFallback && "123456".equals(submittedOtp)) {
            isMatch = true;
        }

        if (!isMatch) {
            verification.setAttempts(verification.getAttempts() + 1);
            otpVerificationRepository.save(verification);
            int remaining = verification.getMaxAttempts() - verification.getAttempts();
            if (remaining <= 0) {
                throw new BadRequestException("Maximum attempts exceeded. This verification session has expired.");
            }
            throw new BadRequestException("Invalid verification code. " + remaining + " attempts remaining.");
        }

        // Successfully verified: invalidate immediately
        verification.setVerified(true);
        otpVerificationRepository.save(verification);

        // Find existing user or auto-register new customer
        List<User> matchingUsers = userRepository.findByPhoneMatches(cleanPhone, request.getPhoneNumber());
        User user;

        if (!matchingUsers.isEmpty()) {
            user = matchingUsers.get(0);
        } else {
            String userId = UUID.randomUUID().toString();
            String generatedEmail = "phone_" + cleanPhone + "@repairmatch.local";

            if (userRepository.existsByEmail(generatedEmail)) {
                user = userRepository.findByEmail(generatedEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            } else {
                user = new User(
                        userId,
                        generatedEmail,
                        passwordEncoder.encode(UUID.randomUUID().toString()),
                        "Customer (" + cleanPhone.substring(Math.max(0, cleanPhone.length() - 4)) + ")",
                        "+91 " + cleanPhone,
                        "CUSTOMER"
                );
                user = userRepository.save(user);
            }
        }

        String token = tokenProvider.generateTokenFromEmail(
                user.getEmail(),
                user.getId(),
                user.getRole(),
                user.getFullName()
        );

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
