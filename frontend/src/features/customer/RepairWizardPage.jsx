import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import {
  Wrench, CheckCircle2, ChevronRight, ChevronLeft, MapPin, Calendar,
  Clock, ShieldCheck, Star, AlertCircle, ArrowRight, User, Check,
  Info, ExternalLink, X, Award, ThumbsUp
} from 'lucide-react';
import StarRating from '../../components/common/StarRating';
import Badge from '../../components/common/Badge';

const TIME_SLOTS = [
  '09:00 AM - 12:00 PM',
  '12:00 PM - 03:00 PM',
  '03:00 PM - 06:00 PM',
  '06:00 PM - 09:00 PM'
];

export default function RepairWizardPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  const [step, setStep] = useState(1);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);

  // Step 2 & 3 Data
  const [brands, setBrands] = useState([]);
  const [models, setModels] = useState([]);
  const [problemTypes, setProblemTypes] = useState([]);

  // Selections
  const [selectedBrandId, setSelectedBrandId] = useState('');
  const [selectedModelId, setSelectedModelId] = useState('');
  const [selectedProblemTypeId, setSelectedProblemTypeId] = useState('');
  const [problemDescription, setProblemDescription] = useState('');

  // Step 4 Data
  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState('');
  const [manualAddress, setManualAddress] = useState({
    street: '12th Main, Indiranagar',
    city: 'Bengaluru',
    state: 'Karnataka',
    postalCode: '560038',
    latitude: 12.9719,
    longitude: 77.6412
  });
  const [useManualAddress, setUseManualAddress] = useState(false);
  const [scheduledDate, setScheduledDate] = useState('2026-09-24');
  const [timeSlot, setTimeSlot] = useState(TIME_SLOTS[0]);

  // Step 5 Data (Matching)
  const [matches, setMatches] = useState([]);
  const [matchingLoading, setMatchingLoading] = useState(false);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [error, setError] = useState('');

  // Profile / Reviews Modal
  const [profileModalOpen, setProfileModalOpen] = useState(false);
  const [selectedTechProfile, setSelectedTechProfile] = useState(null);
  const [techReviews, setTechReviews] = useState([]);
  const [loadingReviews, setLoadingReviews] = useState(false);

  // Load all categories on mount
  useEffect(() => {
    api.get('/catalog/categories').then((res) => {
      setCategories(res.data);
      const preselectedId = searchParams.get('category');
      if (preselectedId) {
        const found = res.data.find(c => c.id === preselectedId);
        if (found) handleSelectCategory(found);
      }
    });

    if (isAuthenticated) {
      api.get('/customer/addresses').then((res) => {
        setAddresses(res.data);
        const defaultAddr = res.data.find(a => a.default) || res.data[0];
        if (defaultAddr) setSelectedAddressId(defaultAddr.id);
        else setUseManualAddress(true);
      }).catch(() => setUseManualAddress(true));
    } else {
      setUseManualAddress(true);
    }
  }, [searchParams, isAuthenticated]);

  const handleSelectCategory = (cat) => {
    setSelectedCategory(cat);
    setSelectedBrandId('');
    setSelectedModelId('');
    setSelectedProblemTypeId('');
    setError('');

    // Fetch brands and problems for this category
    api.get(`/catalog/categories/${cat.id}`).then((res) => {
      setBrands(res.data.brands || []);
      setProblemTypes(res.data.problemTypes || []);
      setStep(cat.requiresBrandAndModel ? 2 : 3);
    });
  };

  const handleBrandChange = (brandId) => {
    setSelectedBrandId(brandId);
    setSelectedModelId('');
    if (brandId && selectedCategory) {
      api.get(`/catalog/categories/${selectedCategory.id}/brands/${brandId}/models`)
        .then((res) => setModels(res.data))
        .catch(() => setModels([]));
    } else {
      setModels([]);
    }
  };

  const executeMatching = async () => {
    setError('');
    setMatchingLoading(true);

    let lat = manualAddress.latitude;
    let lon = manualAddress.longitude;

    if (!useManualAddress && selectedAddressId) {
      const addr = addresses.find(a => a.id === selectedAddressId);
      if (addr) {
        lat = addr.latitude;
        lon = addr.longitude;
      }
    }

    const payload = {
      categoryId: selectedCategory.id,
      brandId: selectedBrandId || null,
      modelId: selectedModelId || null,
      problemTypeId: selectedProblemTypeId || null,
      customerLatitude: lat,
      customerLongitude: lon,
      scheduledDate,
      timeSlot
    };

    try {
      const res = await api.post('/matching/find', payload);
      setMatches(res.data);
      setStep(5);
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to complete matching calculation. Please check your inputs.');
    } finally {
      setMatchingLoading(false);
    }
  };

  const handleBookTechnician = async (technicianId) => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: '/wizard' } });
      return;
    }

    setBookingLoading(true);
    setError('');

    try {
      let finalAddressId = selectedAddressId;

      if (useManualAddress || !finalAddressId) {
        const addrRes = await api.post('/customer/addresses', {
          street: manualAddress.street,
          city: manualAddress.city,
          state: manualAddress.state,
          postalCode: manualAddress.postalCode,
          latitude: manualAddress.latitude,
          longitude: manualAddress.longitude,
          default: true
        });
        finalAddressId = addrRes.data.id;
      }

      const bookingPayload = {
        technicianId,
        categoryId: selectedCategory.id,
        brandId: selectedBrandId || null,
        modelId: selectedModelId || null,
        problemTypeId: selectedProblemTypeId || null,
        problemDescription: problemDescription || 'Inspection and diagnostic repair requested.',
        addressId: finalAddressId,
        scheduledDate,
        timeSlot
      };

      await api.post('/bookings', bookingPayload);
      navigate('/customer/bookings');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to confirm booking. Please try again.');
    } finally {
      setBookingLoading(false);
    }
  };

  const openProfileModal = async (tech) => {
    setSelectedTechProfile(tech);
    setProfileModalOpen(true);
    setLoadingReviews(true);
    try {
      const res = await api.get(`/reviews/technician/${tech.technicianId}`);
      setTechReviews(res.data || []);
    } catch (e) {
      setTechReviews([]);
    } finally {
      setLoadingReviews(false);
    }
  };

  const selectedBrandObj = brands.find(b => b.id === selectedBrandId);
  const selectedModelObj = models.find(m => m.id === selectedModelId);

  // Stepper labels
  const stepsList = [
    { num: 1, label: 'Category' },
    ...(selectedCategory?.requiresBrandAndModel ? [{ num: 2, label: 'Device' }] : []),
    { num: 3, label: 'Diagnosis' },
    { num: 4, label: 'Schedule' },
    { num: 5, label: 'Matches' }
  ];

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 py-10 px-4 sm:px-6 lg:px-8 transition-colors duration-200">
      <div className="max-w-4xl mx-auto">
        {/* Breadcrumb / Stepper */}
        <div className="mb-8">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-3 sm:p-4 shadow-sm">
            <div className="flex items-center justify-between">
              {stepsList.map((s, idx) => {
                const isActive = step === s.num;
                const isPassed = step > s.num;
                return (
                  <React.Fragment key={s.num}>
                    <div className="flex items-center gap-2">
                      <div
                        className={`w-7 h-7 rounded-full text-xs font-semibold flex items-center justify-center transition-colors ${
                          isActive
                            ? 'bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 shadow-sm'
                            : isPassed
                            ? 'bg-teal-700 dark:bg-teal-600 text-white'
                            : 'bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 border border-slate-200 dark:border-slate-700'
                        }`}
                      >
                        {isPassed ? <Check className="w-3.5 h-3.5" /> : s.num}
                      </div>
                      <span
                        className={`text-xs font-medium hidden sm:inline ${
                          isActive
                            ? 'text-slate-900 dark:text-slate-100 font-semibold'
                            : isPassed
                            ? 'text-slate-700 dark:text-slate-300'
                            : 'text-slate-400 dark:text-slate-500'
                        }`}
                      >
                        {s.label}
                      </span>
                    </div>
                    {idx < stepsList.length - 1 && (
                      <div className="flex-1 mx-2 sm:mx-3 h-[1px] bg-slate-200 dark:bg-slate-800" />
                    )}
                  </React.Fragment>
                );
              })}
            </div>
          </div>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-850 text-rose-800 dark:text-rose-300 text-xs rounded-xl flex items-start gap-2.5">
            <AlertCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 flex-shrink-0 mt-0.5" />
            <div className="flex-1 font-medium">{error}</div>
          </div>
        )}

        {/* STEP 1: CATEGORY SELECTION */}
        {step === 1 && (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-800 transition-colors">
            <div className="max-w-xl mb-6">
              <span className="text-[11px] font-semibold text-teal-800 dark:text-teal-400 uppercase tracking-wider">Step 1 of 5</span>
              <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100 mt-1">Select Service Category</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-1.5 leading-relaxed">
                Choose the appliance, electronic device, or household system requiring maintenance or repair.
              </p>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
              {categories.map((cat) => {
                const isSelected = selectedCategory?.id === cat.id;
                return (
                  <button
                    key={cat.id}
                    type="button"
                    onClick={() => handleSelectCategory(cat)}
                    className={`p-4 rounded-xl border text-left transition-all group focus:outline-none ${
                      isSelected
                        ? 'border-teal-700 dark:border-teal-500 bg-teal-50/40 dark:bg-teal-950/40 ring-1 ring-teal-700/30'
                        : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 bg-white dark:bg-slate-900 hover:bg-slate-50/50 dark:hover:bg-slate-800/50'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs font-semibold text-slate-900 dark:text-slate-100 group-hover:text-teal-800 dark:group-hover:text-teal-400">
                        {cat.name}
                      </span>
                      {isSelected && <CheckCircle2 className="w-4 h-4 text-teal-700 dark:text-teal-400" />}
                    </div>
                    <div className="text-[11px] text-slate-500 dark:text-slate-400 leading-snug">
                      {cat.requiresBrandAndModel ? 'Brand & model specific' : 'General repair service'}
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* STEP 2: BRAND & MODEL (Conditional) */}
        {step === 2 && selectedCategory?.requiresBrandAndModel && (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-800 transition-colors">
            <div className="mb-6">
              <div className="inline-flex items-center gap-1.5 text-xs font-medium text-teal-800 dark:text-teal-300 bg-teal-50 dark:bg-teal-950/60 border border-teal-200/60 dark:border-teal-800/80 px-2.5 py-1 rounded-md mb-2">
                <span>Category:</span>
                <span className="font-semibold text-slate-900 dark:text-slate-100">{selectedCategory.name}</span>
              </div>
              <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">Specify Device Manufacturer &amp; Model</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-1.5 leading-relaxed max-w-2xl">
                Technicians matched through RepairMatch carry specialized tools and OEM-certified parts matched specifically to your exact hardware model.
              </p>
            </div>

            <div className="space-y-4 max-w-lg">
              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                  Manufacturer / Brand <span className="text-rose-500">*</span>
                </label>
                <select
                  value={selectedBrandId}
                  onChange={(e) => handleBrandChange(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
                >
                  <option value="">Select brand (e.g. Apple, Samsung, LG, Sony)...</option>
                  {brands.map((b) => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </div>

              {selectedBrandId && (
                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="block text-xs font-medium text-slate-700 dark:text-slate-300">
                      Model Specification <span className="text-slate-400 font-normal">(Optional)</span>
                    </label>
                    <span className="text-[11px] text-slate-400">Enables parts pre-stocking</span>
                  </div>
                  <select
                    value={selectedModelId}
                    onChange={(e) => setSelectedModelId(e.target.value)}
                    className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
                  >
                    <option value="">Select exact model if known...</option>
                    {models.map((m) => (
                      <option key={m.id} value={m.id}>{m.name}</option>
                    ))}
                  </select>
                </div>
              )}
            </div>

            <div className="mt-8 pt-5 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <button
                type="button"
                onClick={() => setStep(1)}
                className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
              >
                Back
              </button>
              <button
                type="button"
                disabled={!selectedBrandId}
                onClick={() => setStep(3)}
                className="px-5 py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-semibold shadow-sm transition-colors flex items-center gap-1.5 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                Continue to Diagnosis <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        )}

        {/* STEP 3: PROBLEM DETAILS */}
        {step === 3 && (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-800 transition-colors">
            <div className="mb-6">
              <div className="flex flex-wrap items-center gap-2 mb-2">
                <span className="text-[11px] font-medium text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">
                  {selectedCategory?.name}
                </span>
                {selectedBrandObj && (
                  <span className="text-[11px] font-medium text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">
                    {selectedBrandObj.name} {selectedModelObj ? `• ${selectedModelObj.name}` : ''}
                  </span>
                )}
              </div>
              <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">What Issue Are You Experiencing?</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-1.5 leading-relaxed">
                Select from common verified diagnostic symptoms or describe the fault below to ensure the technician brings the appropriate diagnostic kit.
              </p>
            </div>

            {problemTypes.length > 0 && (
              <div className="mb-6">
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-2.5">
                  Common Identified Faults (Click to select)
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                  {problemTypes.map((p) => {
                    const isSelected = selectedProblemTypeId === p.id;
                    return (
                      <div
                        key={p.id}
                        onClick={() => setSelectedProblemTypeId(isSelected ? '' : p.id)}
                        className={`p-3.5 rounded-lg border text-left cursor-pointer transition-all ${
                          isSelected
                            ? 'border-teal-700 dark:border-teal-500 bg-teal-50/40 dark:bg-teal-950/40 ring-1 ring-teal-700/30'
                            : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 bg-white dark:bg-slate-900/60'
                        }`}
                      >
                        <div className="flex items-start justify-between gap-2">
                          <div className="font-semibold text-xs text-slate-900 dark:text-slate-100">{p.title}</div>
                          <span className="text-[10px] font-medium text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded flex-shrink-0">
                            Est: ₹{p.typicalPriceEstimate}
                          </span>
                        </div>
                        <div className="text-[11px] text-slate-500 dark:text-slate-400 mt-1 leading-snug">{p.description}</div>
                      </div>
                    );
                  })}
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                Fault Description <span className="text-rose-500">*</span>
              </label>
              <textarea
                rows={3}
                required
                value={problemDescription}
                onChange={(e) => setProblemDescription(e.target.value)}
                placeholder="Describe how the malfunction occurred, physical damage symptoms, warning lights, or unusual noises..."
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900 dark:focus:border-slate-100"
              />
              <span className="text-[11px] text-slate-400 dark:text-slate-500 mt-1 block">
                Be as detailed as possible to allow accurate matching against technician expertise histories.
              </span>
            </div>

            <div className="mt-8 pt-5 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <button
                type="button"
                onClick={() => setStep(selectedCategory?.requiresBrandAndModel ? 2 : 1)}
                className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
              >
                Back
              </button>
              <button
                type="button"
                disabled={!problemDescription.trim()}
                onClick={() => setStep(4)}
                className="px-5 py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-semibold shadow-sm transition-colors flex items-center gap-1.5 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                Continue to Scheduling <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        )}

        {/* STEP 4: LOCATION & SCHEDULING */}
        {step === 4 && (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-800 transition-colors">
            <div className="mb-6">
              <span className="text-[11px] font-semibold text-teal-800 dark:text-teal-400 uppercase tracking-wider">Step 4 of 5</span>
              <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100 mt-1">Service Location &amp; Appointment Slot</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-1.5 leading-relaxed">
                Technicians are strictly evaluated against their verified operating radius from your physical doorstep.
              </p>
            </div>

            {/* Saved Address Selection vs Manual */}
            {addresses.length > 0 && !useManualAddress ? (
              <div className="mb-6">
                <div className="flex items-center justify-between mb-2">
                  <label className="block text-xs font-medium text-slate-700 dark:text-slate-300">Select Service Address</label>
                  <button
                    type="button"
                    onClick={() => setUseManualAddress(true)}
                    className="text-xs font-medium text-teal-800 dark:text-teal-400 hover:underline"
                  >
                    + Enter different address
                  </button>
                </div>
                <div className="space-y-2">
                  {addresses.map((addr) => (
                    <label
                      key={addr.id}
                      className={`flex items-start justify-between p-3.5 rounded-lg border cursor-pointer transition-colors ${
                        selectedAddressId === addr.id
                          ? 'border-teal-700 dark:border-teal-500 bg-teal-50/40 dark:bg-teal-950/40 ring-1 ring-teal-700/30'
                          : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
                      }`}
                    >
                      <div className="flex items-start gap-3">
                        <input
                          type="radio"
                          name="address"
                          checked={selectedAddressId === addr.id}
                          onChange={() => setSelectedAddressId(addr.id)}
                          className="mt-0.5 text-teal-700 dark:text-teal-500 focus:ring-teal-700"
                        />
                        <div>
                          <div className="text-xs font-semibold text-slate-900 dark:text-slate-100">{addr.street}</div>
                          <div className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                            {addr.city}, {addr.state} • PIN: {addr.postalCode}
                          </div>
                        </div>
                      </div>
                      {addr.default && (
                        <span className="text-[10px] font-medium bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 px-2 py-0.5 rounded border border-slate-200 dark:border-slate-700">
                          Default
                        </span>
                      )}
                    </label>
                  ))}
                </div>
              </div>
            ) : (
              <div className="mb-6 p-4 bg-slate-50/80 dark:bg-slate-850/60 border border-slate-200 dark:border-slate-800 rounded-lg space-y-3">
                <div className="flex items-center justify-between">
                  <div className="text-xs font-semibold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                    <MapPin className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400" />
                    Doorstep Coordinates &amp; Address
                  </div>
                  {addresses.length > 0 && (
                    <button
                      type="button"
                      onClick={() => setUseManualAddress(false)}
                      className="text-xs font-medium text-teal-800 dark:text-teal-400 hover:underline"
                    >
                      Choose saved address
                    </button>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-[11px] font-medium text-slate-700 dark:text-slate-300 mb-1">Street Address</label>
                    <input
                      type="text"
                      value={manualAddress.street}
                      onChange={(e) => setManualAddress({ ...manualAddress, street: e.target.value })}
                      className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-md text-xs text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-medium text-slate-700 dark:text-slate-300 mb-1">City</label>
                    <input
                      type="text"
                      value={manualAddress.city}
                      onChange={(e) => setManualAddress({ ...manualAddress, city: e.target.value })}
                      className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-md text-xs text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-medium text-slate-700 dark:text-slate-300 mb-1">Latitude (Radius Search)</label>
                    <input
                      type="number"
                      step="0.0001"
                      value={manualAddress.latitude}
                      onChange={(e) => setManualAddress({ ...manualAddress, latitude: parseFloat(e.target.value) })}
                      className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-md text-xs font-mono text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-medium text-slate-700 dark:text-slate-300 mb-1">Longitude</label>
                    <input
                      type="number"
                      step="0.0001"
                      value={manualAddress.longitude}
                      onChange={(e) => setManualAddress({ ...manualAddress, longitude: parseFloat(e.target.value) })}
                      className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-md text-xs font-mono text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Date & Slot */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                  Preferred Appointment Date
                </label>
                <div className="relative">
                  <input
                    type="date"
                    value={scheduledDate}
                    onChange={(e) => setScheduledDate(e.target.value)}
                    className="w-full px-3.5 py-2 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                  Time Slot Window
                </label>
                <select
                  value={timeSlot}
                  onChange={(e) => setTimeSlot(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-100 bg-white dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                >
                  {TIME_SLOTS.map((slot) => (
                    <option key={slot} value={slot}>{slot}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="mt-8 pt-5 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <button
                type="button"
                onClick={() => setStep(3)}
                className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
              >
                Back
              </button>
              <button
                type="button"
                disabled={matchingLoading}
                onClick={executeMatching}
                className="px-5 py-2.5 bg-teal-800 hover:bg-teal-900 dark:bg-teal-700 dark:hover:bg-teal-600 text-white rounded-lg text-xs font-semibold shadow-sm transition-colors flex items-center gap-2 disabled:opacity-50"
              >
                {matchingLoading ? (
                  <>
                    <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    <span>Evaluating Candidate Pool...</span>
                  </>
                ) : (
                  <>
                    <span>Calculate Technician Matches</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </>
                )}
              </button>
            </div>
          </div>
        )}

        {/* STEP 5: MATCHING RESULTS */}
        {step === 5 && (
          <div>
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
              <div>
                <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">Ranked Technician Matches</h2>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
                  Ranked by brand certification, problem domain history, physical radius distance, and verified customer ratings.
                </p>
              </div>
              <button
                onClick={() => setStep(4)}
                className="text-xs font-medium text-teal-800 dark:text-teal-400 hover:underline self-start sm:self-auto"
              >
                ← Change time or location
              </button>
            </div>

            {matches.length === 0 ? (
              <div className="bg-white dark:bg-slate-900 rounded-xl p-12 text-center border border-slate-200 dark:border-slate-800 shadow-sm">
                <AlertCircle className="w-10 h-10 text-slate-400 dark:text-slate-500 mx-auto mb-3" />
                <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">No Eligible Technicians in Range</h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 max-w-md mx-auto mt-1.5 mb-6 leading-relaxed">
                  No verified technicians serving {selectedCategory?.name} are currently available in your operating radius for the selected appointment slot.
                </p>
                <button
                  onClick={() => setStep(4)}
                  className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 text-xs font-medium rounded-lg transition-colors"
                >
                  Adjust Date, Slot or Address
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                {matches.map((tech, idx) => (
                  <div
                    key={tech.technicianId}
                    className={`bg-white dark:bg-slate-900 rounded-xl p-5 sm:p-6 border transition-all ${
                      idx === 0
                        ? 'border-slate-300 dark:border-slate-700 ring-1 ring-slate-900/5 dark:ring-slate-100/5 shadow-sm'
                        : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 shadow-sm'
                    }`}
                  >
                    <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
                      {/* Left: Info */}
                      <div className="flex-1">
                        <div className="flex flex-wrap items-center gap-2 mb-2">
                          <span className="font-semibold text-base text-slate-900 dark:text-slate-100">{tech.name}</span>
                          <span className="inline-flex items-center gap-1 text-[11px] font-medium text-teal-800 dark:text-teal-300 bg-teal-50 dark:bg-teal-950/60 border border-teal-200/60 dark:border-teal-800/80 px-2 py-0.5 rounded">
                            <ShieldCheck className="w-3 h-3 text-teal-700 dark:text-teal-400" /> KYC Verified
                          </span>
                          {idx === 0 && (
                            <span className="text-[10px] font-semibold text-slate-700 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">
                              Top Match
                            </span>
                          )}
                          <span className="text-xs font-semibold text-slate-900 dark:text-slate-100 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded ml-auto lg:ml-0">
                            {tech.suitabilityScore}% Match Score
                          </span>
                        </div>

                        <p className="text-xs text-slate-600 dark:text-slate-400 line-clamp-2 leading-relaxed mb-3">
                          {tech.bio}
                        </p>

                        {/* Verified Credential Badges */}
                        <div className="flex flex-wrap items-center gap-x-4 gap-y-1.5 text-xs text-slate-600 dark:text-slate-400 mb-3">
                          <span className="flex items-center gap-1">
                            <Star className="w-3.5 h-3.5 text-amber-500 fill-amber-500" />
                            <span className="font-semibold text-slate-900 dark:text-slate-100">
                              {tech.averageRating > 0 ? tech.averageRating.toFixed(1) : 'New'}
                            </span>
                            <span className="text-slate-400 dark:text-slate-500">({tech.totalReviews} reviews)</span>
                          </span>
                          <span className="text-slate-300 dark:text-slate-700">•</span>
                          <span>{tech.experienceYears} yrs experience</span>
                          <span className="text-slate-300 dark:text-slate-700">•</span>
                          <span>{tech.completedJobsCount} repairs completed</span>
                          <span className="text-slate-300 dark:text-slate-700">•</span>
                          <span className="flex items-center gap-1">
                            <MapPin className="w-3 h-3 text-slate-400" />
                            {tech.distanceKm} km away
                          </span>
                        </div>

                        {/* Reasons for match */}
                        <div className="flex flex-wrap gap-1.5 pt-2 border-t border-slate-100 dark:border-slate-800">
                          {tech.brandMatched && (
                            <span className="inline-flex items-center gap-1 text-[11px] font-medium text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 px-2 py-0.5 rounded">
                              <Check className="w-3 h-3 text-teal-700 dark:text-teal-400" /> Brand Experience
                            </span>
                          )}
                          {tech.problemMatched && (
                            <span className="inline-flex items-center gap-1 text-[11px] font-medium text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 px-2 py-0.5 rounded">
                              <Check className="w-3 h-3 text-teal-700 dark:text-teal-400" /> Problem Specialist
                            </span>
                          )}
                          {tech.matchHighlights?.map((badge, bIdx) => (
                            <span
                              key={bIdx}
                              className="inline-flex items-center gap-1 text-[11px] font-medium text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 px-2 py-0.5 rounded"
                            >
                              <Check className="w-3 h-3 text-teal-700 dark:text-teal-400" /> {badge}
                            </span>
                          ))}
                        </div>
                      </div>

                      {/* Right: Fee & Action */}
                      <div className="lg:text-right border-t lg:border-t-0 pt-4 lg:pt-0 border-slate-100 dark:border-slate-800 flex flex-row lg:flex-col justify-between items-end gap-3 flex-shrink-0">
                        <div>
                          <div className="text-[11px] text-slate-500 dark:text-slate-400 font-medium">Standard Diagnostic Fee</div>
                          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-0.5">₹{tech.baseInspectionFee}</div>
                          <div className="text-[10px] text-slate-400 dark:text-slate-500 mt-0.5">Credited if repair proceeds</div>
                        </div>

                        <div className="flex items-center gap-2">
                          <button
                            type="button"
                            onClick={() => openProfileModal(tech)}
                            className="px-3.5 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 text-slate-700 dark:text-slate-300 text-xs font-medium rounded-lg hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                          >
                            View Reviews
                          </button>
                          <button
                            type="button"
                            disabled={bookingLoading}
                            onClick={() => handleBookTechnician(tech.technicianId)}
                            className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium text-xs rounded-lg shadow-sm transition-colors disabled:opacity-50"
                          >
                            {bookingLoading ? 'Confirming...' : 'Book Technician'}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Modal: View Technician Profile & Reviews */}
        {profileModalOpen && selectedTechProfile && (
          <div className="fixed inset-0 z-50 bg-slate-950/40 dark:bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="bg-white dark:bg-slate-900 rounded-xl max-w-lg w-full p-6 shadow-xl border border-slate-200 dark:border-slate-800 max-h-[90vh] overflow-y-auto transition-colors">
              <div className="flex items-start justify-between pb-4 border-b border-slate-100 dark:border-slate-800">
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-lg font-semibold text-slate-900 dark:text-slate-100">{selectedTechProfile.name}</h3>
                    <span className="text-[11px] font-medium text-teal-800 dark:text-teal-300 bg-teal-50 dark:bg-teal-950/60 border border-teal-200/60 dark:border-teal-800/80 px-2 py-0.5 rounded flex items-center gap-1">
                      <ShieldCheck className="w-3 h-3 text-teal-700 dark:text-teal-400" /> Verified
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{selectedTechProfile.experienceYears} Years Field Experience</p>
                </div>
                <button
                  type="button"
                  onClick={() => setProfileModalOpen(false)}
                  className="p-1 rounded-md text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              <div className="py-4 space-y-4">
                <div>
                  <h4 className="text-xs font-semibold text-slate-800 dark:text-slate-200 mb-1">About the Technician</h4>
                  <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed bg-slate-50 dark:bg-slate-800/60 p-3 rounded-lg border border-slate-100 dark:border-slate-800">
                    {selectedTechProfile.bio}
                  </p>
                </div>

                <div className="grid grid-cols-3 gap-3 text-center">
                  <div className="p-2.5 bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 rounded-lg">
                    <div className="text-[11px] text-slate-500 dark:text-slate-400">Rating</div>
                    <div className="text-sm font-bold text-slate-900 dark:text-slate-100 mt-0.5 flex items-center justify-center gap-1">
                      <Star className="w-3.5 h-3.5 text-amber-500 fill-amber-500" />
                      {selectedTechProfile.averageRating > 0 ? selectedTechProfile.averageRating.toFixed(1) : 'New'}
                    </div>
                  </div>
                  <div className="p-2.5 bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 rounded-lg">
                    <div className="text-[11px] text-slate-500 dark:text-slate-400">Completed</div>
                    <div className="text-sm font-bold text-slate-900 dark:text-slate-100 mt-0.5">
                      {selectedTechProfile.completedJobsCount} jobs
                    </div>
                  </div>
                  <div className="p-2.5 bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 rounded-lg">
                    <div className="text-[11px] text-slate-500 dark:text-slate-400">Distance</div>
                    <div className="text-sm font-bold text-slate-900 dark:text-slate-100 mt-0.5">
                      {selectedTechProfile.distanceKm} km
                    </div>
                  </div>
                </div>

                <div>
                  <h4 className="text-xs font-semibold text-slate-800 dark:text-slate-200 mb-2">Verified Customer Reviews ({techReviews.length})</h4>
                  {loadingReviews ? (
                    <div className="text-center py-6 text-xs text-slate-400">Loading reviews...</div>
                  ) : techReviews.length === 0 ? (
                    <div className="text-center py-6 text-xs text-slate-400 bg-slate-50 dark:bg-slate-800/60 rounded-lg border border-slate-100 dark:border-slate-800">
                      No public reviews submitted yet.
                    </div>
                  ) : (
                    <div className="space-y-2.5 max-h-48 overflow-y-auto pr-1">
                      {techReviews.map((r) => (
                        <div key={r.id} className="p-3 bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 rounded-lg text-xs">
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-semibold text-slate-900 dark:text-slate-100">{r.customerName}</span>
                            <StarRating rating={r.rating} size={13} />
                          </div>
                          <p className="text-slate-600 dark:text-slate-400 mt-0.5 leading-snug">"{r.comment}"</p>
                          <span className="text-[10px] text-slate-400 dark:text-slate-500 mt-1 block">
                            {new Date(r.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                <div>
                  <span className="text-[11px] text-slate-500 dark:text-slate-400">Base Inspection:</span>
                  <span className="text-xs font-bold text-slate-900 dark:text-slate-100 ml-1">₹{selectedTechProfile.baseInspectionFee}</span>
                </div>
                <button
                  type="button"
                  onClick={() => {
                    setProfileModalOpen(false);
                    handleBookTechnician(selectedTechProfile.technicianId);
                  }}
                  className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors"
                >
                  Book This Technician
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
