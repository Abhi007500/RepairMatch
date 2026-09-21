import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Wrench, AlertCircle, ArrowRight, Phone, Mail, KeyRound, CheckCircle2, RefreshCw } from 'lucide-react';

export default function LoginPage() {
  const [authMethod, setAuthMethod] = useState('password'); // 'password' | 'otp'
  
  // Password login state
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  
  // OTP login state
  const [phoneNumber, setPhoneNumber] = useState('');
  const [otp, setOtp] = useState('');
  const [otpStep, setOtpStep] = useState('phone'); // 'phone' | 'verify'
  const [previewOtp, setPreviewOtp] = useState('');
  const [otpSentMsg, setOtpSentMsg] = useState('');
  const [cooldown, setCooldown] = useState(0);
  
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Active cooldown countdown
  useEffect(() => {
    if (cooldown <= 0) return;
    const timer = setInterval(() => {
      setCooldown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(timer);
  }, [cooldown]);

  const { login, sendOtp, loginWithOtp } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const redirectUrl = location.state?.from || '/';

  const handlePostLogin = (user) => {
    if (user.role === 'ADMIN') navigate('/admin');
    else if (user.role === 'TECHNICIAN') navigate('/technician/dashboard');
    else navigate(redirectUrl === '/login' ? '/' : redirectUrl);
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const user = await login(email, password);
      handlePostLogin(user);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleSendOtp = async (e) => {
    if (e) e.preventDefault();
    setError('');

    const clean = phoneNumber.trim().replace(/[^0-9]/g, '');
    if (clean.length !== 10 || !/^[6-9]\d{9}$/.test(clean)) {
      setError('Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9.');
      return;
    }

    if (cooldown > 0) {
      setError(`Please wait ${cooldown} seconds before requesting a new code.`);
      return;
    }

    setSubmitting(true);
    try {
      const res = await sendOtp(clean);
      const code = res.demoOtp || res.previewOtp || '123456';
      setPreviewOtp(code);
      setOtp(code); // Pre-fill for instantaneous testing
      setOtpSentMsg(res.message || 'OTP sent successfully');
      setCooldown(res.cooldownSeconds || 60);
      setOtpStep('verify');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to send OTP. Please check your network connection.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setError('');
    if (!otp || otp.trim().length < 4) {
      setError('Please enter the verification code.');
      return;
    }
    setSubmitting(true);
    try {
      const user = await loginWithOtp(phoneNumber.trim(), otp.trim());
      handlePostLogin(user);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid or expired OTP. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const fillPasswordDemo = (demoEmail, demoPassword) => {
    setAuthMethod('password');
    setEmail(demoEmail);
    setPassword(demoPassword);
    setError('');
  };

  const fillPhoneDemo = (demoPhone) => {
    setAuthMethod('otp');
    setPhoneNumber(demoPhone);
    setOtp('123456');
    setPreviewOtp('123456');
    setOtpStep('verify');
    setOtpSentMsg(`Code sent to +91 ${demoPhone}`);
    setError('');
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors duration-200">
      <div className="max-w-md w-full bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 p-7 sm:p-8 transition-colors">
        <div className="text-center mb-6">
          <div className="w-10 h-10 rounded-lg bg-slate-900 dark:bg-slate-800 text-white flex items-center justify-center mx-auto mb-3 shadow-sm">
            <Wrench className="w-5 h-5 text-slate-100" />
          </div>
          <h2 className="text-xl font-semibold text-slate-900 dark:text-slate-100">Sign In to RepairMatch</h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">Access your service bookings, technician dispatch, or governance</p>
        </div>

        {/* Tab Switcher: Password vs Phone/OTP */}
        <div className="flex rounded-lg bg-slate-100 dark:bg-slate-800/80 p-1 mb-6 border border-slate-200 dark:border-slate-700/60">
          <button
            type="button"
            onClick={() => {
              setAuthMethod('password');
              setError('');
            }}
            className={`flex-1 py-1.5 px-3 rounded-md text-xs font-medium flex items-center justify-center gap-1.5 transition-all ${
              authMethod === 'password'
                ? 'bg-white dark:bg-slate-900 text-slate-900 dark:text-white shadow-sm font-semibold'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            <Mail className="w-3.5 h-3.5" />
            Email & Password
          </button>
          <button
            type="button"
            onClick={() => {
              setAuthMethod('otp');
              setError('');
            }}
            className={`flex-1 py-1.5 px-3 rounded-md text-xs font-medium flex items-center justify-center gap-1.5 transition-all ${
              authMethod === 'otp'
                ? 'bg-white dark:bg-slate-900 text-slate-900 dark:text-white shadow-sm font-semibold'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            <Phone className="w-3.5 h-3.5" />
            Phone Number & OTP
          </button>
        </div>

        {error && (
          <div className="mb-5 p-3.5 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-300 text-xs rounded-lg flex items-start gap-2">
            <AlertCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {/* Option 1: Email & Password Form */}
        {authMethod === 'password' && (
          <form onSubmit={handlePasswordSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">Email Address</label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="e.g. rahul@gmail.com"
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
              />
            </div>

            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300">Password</label>
                <span className="text-[11px] text-slate-400 dark:text-slate-500">Default: password123</span>
              </div>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
              />
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium rounded-lg shadow-sm transition-colors text-xs flex items-center justify-center gap-1.5 disabled:opacity-50 mt-2"
            >
              {submitting ? 'Authenticating...' : 'Sign In with Password'}
              {!submitting && <ArrowRight className="w-3.5 h-3.5" />}
            </button>
          </form>
        )}

        {/* Option 2: Phone Number & OTP Flow */}
        {authMethod === 'otp' && (
          <div>
            {otpStep === 'phone' ? (
              <form onSubmit={handleSendOtp} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                    Mobile Phone Number
                  </label>
                  <div className="relative flex rounded-lg shadow-sm">
                    <span className="inline-flex items-center px-3 rounded-l-lg border border-r-0 border-slate-300 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-xs font-medium select-none">
                      🇮🇳 +91
                    </span>
                    <input
                      type="tel"
                      required
                      value={phoneNumber}
                      onChange={(e) => setPhoneNumber(e.target.value.replace(/[^0-9]/g, '').slice(0, 10))}
                      placeholder="98765 43210"
                      maxLength={10}
                      className="w-full px-3.5 py-2.5 rounded-r-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100 tracking-wider"
                    />
                  </div>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1.5">
                    We will send a 6-digit one-time verification code via SMS.
                  </p>
                  <div className="mt-2.5 p-2.5 bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-md text-[11px] text-slate-600 dark:text-slate-300">
                    💡 <strong>Local Environment:</strong> Real SMS requires a paid SMS gateway. The verification code will be shown directly on your screen (or use universal code <span className="font-mono font-bold text-slate-900 dark:text-white">123456</span>).
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={submitting || phoneNumber.length < 10}
                  className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium rounded-lg shadow-sm transition-colors text-xs flex items-center justify-center gap-1.5 disabled:opacity-50 mt-2"
                >
                  {submitting ? 'Sending Code...' : 'Get Verification Code'}
                  {!submitting && <ArrowRight className="w-3.5 h-3.5" />}
                </button>
              </form>
            ) : (
              <form onSubmit={handleVerifyOtp} className="space-y-4">
                <div className="p-3.5 bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 rounded-lg text-xs text-blue-900 dark:text-blue-200 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="font-semibold flex items-center gap-1.5 text-blue-800 dark:text-blue-300">
                      <CheckCircle2 className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                      Verification Code for +91 {phoneNumber}
                    </span>
                    <button
                      type="button"
                      onClick={() => {
                        setOtpStep('phone');
                        setError('');
                      }}
                      className="text-[11px] font-semibold text-blue-700 dark:text-blue-300 hover:underline flex-shrink-0"
                    >
                      Change Number
                    </button>
                  </div>
                  <p className="text-[11px] text-blue-700 dark:text-blue-300">
                    Running locally: no real SMS is sent to your phone carrier. Use the auto-filled code or <strong>123456</strong>:
                  </p>
                  <div className="flex items-center gap-2 pt-0.5">
                    <span className="px-2.5 py-1 bg-white dark:bg-slate-900 border border-blue-300 dark:border-blue-700 rounded font-mono font-bold text-sm tracking-widest text-blue-950 dark:text-white select-all">
                      {previewOtp || '123456'}
                    </span>
                    <button
                      type="button"
                      onClick={() => setOtp(previewOtp || '123456')}
                      className="text-[11px] font-semibold text-blue-800 dark:text-blue-300 hover:underline"
                    >
                      Click to re-fill
                    </button>
                  </div>
                </div>

                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="block text-xs font-medium text-slate-700 dark:text-slate-300">
                      Enter 6-Digit OTP
                    </label>
                    <button
                      type="button"
                      onClick={() => handleSendOtp()}
                      disabled={submitting || cooldown > 0}
                      className="text-[11px] text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 flex items-center gap-1 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <RefreshCw className={`w-3 h-3 ${submitting ? 'animate-spin' : ''}`} />
                      {cooldown > 0 ? `Resend in ${cooldown}s` : 'Resend Code'}
                    </button>
                  </div>
                  <input
                    type="text"
                    required
                    autoFocus
                    value={otp}
                    onChange={(e) => setOtp(e.target.value.replace(/[^0-9]/g, '').slice(0, 6))}
                    placeholder="1 2 3 4 5 6"
                    maxLength={6}
                    className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-sm font-mono tracking-widest text-center text-slate-900 dark:text-slate-100 bg-white dark:bg-slate-800 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
                  />
                </div>

                <button
                  type="submit"
                  disabled={submitting || otp.length < 4}
                  className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium rounded-lg shadow-sm transition-colors text-xs flex items-center justify-center gap-1.5 disabled:opacity-50 mt-2"
                >
                  {submitting ? 'Verifying & Signing In...' : 'Verify & Sign In'}
                  {!submitting && <ArrowRight className="w-3.5 h-3.5" />}
                </button>
              </form>
            )}
          </div>
        )}

        {/* 1-Click Demo Accounts */}
        <div className="mt-8 pt-6 border-t border-slate-100 dark:border-slate-800">
          <div className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider text-center mb-3">
            Quick 1-Click Test Accounts ({authMethod === 'password' ? 'Email Mode' : 'Phone Mode'})
          </div>
          <div className="grid grid-cols-3 gap-2">
            <button
              type="button"
              onClick={() => {
                if (authMethod === 'password') {
                  fillPasswordDemo('rahul@gmail.com', 'password123');
                } else {
                  fillPhoneDemo('9876543210');
                }
              }}
              className="px-2.5 py-2 bg-slate-50 dark:bg-slate-800/80 hover:bg-slate-100 dark:hover:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-200 transition-colors text-center"
            >
              <div className="text-[10px] text-slate-400 dark:text-slate-500">Customer</div>
              <div className="font-semibold text-slate-900 dark:text-slate-100 truncate">
                {authMethod === 'password' ? 'Rahul' : '9876543210'}
              </div>
            </button>
            <button
              type="button"
              onClick={() => {
                if (authMethod === 'password') {
                  fillPasswordDemo('rajesh.tech@repairmatch.com', 'password123');
                } else {
                  fillPhoneDemo('9811122233');
                }
              }}
              className="px-2.5 py-2 bg-slate-50 dark:bg-slate-800/80 hover:bg-slate-100 dark:hover:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-200 transition-colors text-center"
            >
              <div className="text-[10px] text-slate-400 dark:text-slate-500">Technician</div>
              <div className="font-semibold text-teal-800 dark:text-teal-400 truncate">
                {authMethod === 'password' ? 'Rajesh' : '9811122233'}
              </div>
            </button>
            <button
              type="button"
              onClick={() => {
                if (authMethod === 'password') {
                  fillPasswordDemo('admin@repairmatch.com', 'password123');
                } else {
                  fillPhoneDemo('9999900001');
                }
              }}
              className="px-2.5 py-2 bg-slate-50 dark:bg-slate-800/80 hover:bg-slate-100 dark:hover:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-200 transition-colors text-center"
            >
              <div className="text-[10px] text-slate-400 dark:text-slate-500">Admin</div>
              <div className="font-semibold text-slate-900 dark:text-slate-100 truncate">
                {authMethod === 'password' ? 'System' : '9999900001'}
              </div>
            </button>
          </div>
        </div>

        <div className="mt-6 text-center text-xs text-slate-500 dark:text-slate-400">
          New to RepairMatch?{' '}
          <Link to="/register" className="font-medium text-slate-900 dark:text-slate-100 hover:underline">
            Create an account
          </Link>
        </div>
      </div>
    </div>
  );
}
