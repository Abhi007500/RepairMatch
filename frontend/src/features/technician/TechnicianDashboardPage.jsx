import React, { useState, useEffect, useMemo } from 'react';
import api from '../../api/client';
import Badge from '../../components/common/Badge';
import StarRating from '../../components/common/StarRating';
import {
  Wrench, CheckCircle2, XCircle, Play, Check, AlertCircle,
  Calendar, Clock, MapPin, DollarSign, Star, User, ShieldCheck,
  Phone, X, FileText, ArrowRight, Activity
} from 'lucide-react';

export default function TechnicianDashboardPage() {
  const [profile, setProfile] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('ACTION_REQUIRED');

  // Complete Job Modal
  const [completeModalOpen, setCompleteModalOpen] = useState(false);
  const [selectedBookingToComplete, setSelectedBookingToComplete] = useState(null);
  const [finalAmount, setFinalAmount] = useState('');
  const [completionRemarks, setCompletionRemarks] = useState('');
  const [submittingComplete, setSubmittingComplete] = useState(false);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [profRes, bookRes] = await Promise.all([
        api.get('/technician/profile'),
        api.get('/bookings/technician')
      ]);
      setProfile(profRes.data);
      setBookings(bookRes.data);

      if (profRes.data?.id) {
        const revRes = await api.get(`/reviews/technician/${profRes.data.id}`);
        setReviews(revRes.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Error loading dashboard data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const handleToggleAvailability = async () => {
    if (!profile) return;
    const newStatus = !profile.available;
    try {
      await api.patch(`/technician/availability?available=${newStatus}`);
      setProfile({ ...profile, available: newStatus });
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update availability status');
    }
  };

  const handleUpdateStatus = async (bookingId, targetStatus, remarks = '') => {
    try {
      await api.put(`/bookings/${bookingId}/status`, {
        status: targetStatus,
        remarks: remarks || `Technician updated status to ${targetStatus}`
      });
      loadDashboardData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update status');
    }
  };

  const handleCompleteSubmit = async (e) => {
    e.preventDefault();
    if (!selectedBookingToComplete) return;

    setSubmittingComplete(true);
    try {
      await api.put(`/bookings/${selectedBookingToComplete.id}/status`, {
        status: 'COMPLETED',
        finalAmount: finalAmount ? parseFloat(finalAmount) : selectedBookingToComplete.inspectionFee,
        remarks: completionRemarks || 'Repair successfully completed on-site'
      });
      setCompleteModalOpen(false);
      setFinalAmount('');
      setCompletionRemarks('');
      loadDashboardData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to complete job');
    } finally {
      setSubmittingComplete(false);
    }
  };

  // Filter bookings by tab
  const pendingCount = bookings.filter(b => b.status === 'PENDING').length;
  const inProgressCount = bookings.filter(b => ['ACCEPTED', 'IN_PROGRESS'].includes(b.status)).length;
  const completedCount = bookings.filter(b => b.status === 'COMPLETED').length;

  const filteredBookings = useMemo(() => {
    if (activeTab === 'ACTION_REQUIRED') {
      return bookings.filter(b => b.status === 'PENDING');
    }
    if (activeTab === 'ACTIVE_WORK') {
      return bookings.filter(b => ['ACCEPTED', 'IN_PROGRESS'].includes(b.status));
    }
    if (activeTab === 'COMPLETED') {
      return bookings.filter(b => b.status === 'COMPLETED');
    }
    return bookings;
  }, [bookings, activeTab]);

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] p-8 max-w-6xl mx-auto space-y-4">
        <div className="h-40 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 animate-pulse" />
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="h-24 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 animate-pulse" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 py-10 px-4 sm:px-6 lg:px-8 transition-colors duration-200">
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Profile & Availability Card */}
        <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-7 border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-6 transition-colors">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <h1 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">{profile?.name}</h1>
              <Badge status={profile?.verificationStatus} />
            </div>
            <p className="text-xs text-slate-600 dark:text-slate-400 max-w-xl leading-relaxed">{profile?.bio}</p>
          </div>

          <div className="flex items-center gap-4 bg-slate-50 dark:bg-slate-800/60 p-3.5 rounded-lg border border-slate-200 dark:border-slate-700 self-start sm:self-auto flex-shrink-0">
            <div>
              <div className="text-xs font-semibold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                <span className={`w-2 h-2 rounded-full ${profile?.available ? 'bg-emerald-600 dark:bg-emerald-400' : 'bg-slate-400'}`} />
                {profile?.available ? 'On Duty' : 'Off Duty'}
              </div>
              <div className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                {profile?.available ? 'Available for new matches' : 'Paused from matching pool'}
              </div>
            </div>
            <button
              type="button"
              onClick={handleToggleAvailability}
              className={`relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                profile?.available ? 'bg-slate-900 dark:bg-teal-600' : 'bg-slate-300 dark:bg-slate-700'
              }`}
            >
              <span
                className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                  profile?.available ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>

        {error && (
          <div className="p-4 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-300 text-xs rounded-xl flex items-start gap-2.5">
            <AlertCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {/* 4 Metrics Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Customer Rating</div>
            <div className="flex items-center gap-2 mt-1">
              <span className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100">
                {profile?.averageRating > 0 ? profile.averageRating.toFixed(1) : 'New'}
              </span>
              <Star className="w-4 h-4 text-amber-500 fill-amber-500" />
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">{profile?.totalReviews} verified reviews</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Repairs Completed</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              {profile?.completedJobsCount}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Total lifetime jobs</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Diagnostic Fee</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              ₹{profile?.baseInspectionFee}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">{profile?.serviceRadiusKm} km coverage radius</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-4 sm:p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Specializations</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              {profile?.categories?.length || 0}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Approved categories</div>
          </div>
        </div>

        {/* Job Management Section */}
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-lg font-semibold text-slate-900 dark:text-slate-100">Assigned Service Requests</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Review incoming client bookings, accept service orders, and record repair milestones.
              </p>
            </div>
          </div>

          {/* Tabs */}
          <div className="flex items-center gap-1 border-b border-slate-200 dark:border-slate-800 pb-px text-xs">
            <button
              onClick={() => setActiveTab('ACTION_REQUIRED')}
              className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
                activeTab === 'ACTION_REQUIRED'
                  ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                  : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              Action Required ({pendingCount})
            </button>
            <button
              onClick={() => setActiveTab('ACTIVE_WORK')}
              className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
                activeTab === 'ACTIVE_WORK'
                  ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                  : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              In Service ({inProgressCount})
            </button>
            <button
              onClick={() => setActiveTab('COMPLETED')}
              className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
                activeTab === 'COMPLETED'
                  ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                  : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              Completed ({completedCount})
            </button>
            <button
              onClick={() => setActiveTab('ALL')}
              className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
                activeTab === 'ALL'
                  ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                  : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              All Bookings ({bookings.length})
            </button>
          </div>

          {/* Bookings List */}
          {filteredBookings.length === 0 ? (
            <div className="bg-white dark:bg-slate-900 rounded-xl p-10 text-center border border-slate-200 dark:border-slate-800 shadow-sm">
              <Wrench className="w-8 h-8 text-slate-300 dark:text-slate-600 mx-auto mb-2" />
              <div className="text-sm font-semibold text-slate-800 dark:text-slate-200">No requests in this view</div>
              <div className="text-xs text-slate-400 dark:text-slate-500 mt-0.5">
                {activeTab === 'ACTION_REQUIRED'
                  ? 'You have no pending requests awaiting your acceptance.'
                  : 'No bookings match this filter tab.'}
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredBookings.map((b) => (
                <div
                  key={b.id}
                  className="bg-white dark:bg-slate-900 rounded-xl p-5 sm:p-6 border border-slate-200 dark:border-slate-800 shadow-sm hover:border-slate-300 dark:hover:border-slate-700 transition-all"
                >
                  <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
                    {/* Details */}
                    <div className="flex-1 space-y-3">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="font-mono text-xs font-semibold text-slate-900 dark:text-slate-100 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">
                          {b.bookingReference}
                        </span>
                        <Badge status={b.status} />
                        <span className="text-xs font-medium text-slate-700 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">
                          {b.categoryName}
                        </span>
                        {b.brandName && (
                          <span className="text-xs text-slate-500 dark:text-slate-400">
                            • {b.brandName} {b.modelName ? `(${b.modelName})` : ''}
                          </span>
                        )}
                      </div>

                      <div className="flex items-center gap-3 text-xs">
                        <span className="font-semibold text-slate-900 dark:text-slate-100">{b.customerName}</span>
                        {b.customerPhone && (
                          <span className="flex items-center gap-1 text-slate-600 dark:text-slate-400">
                            <Phone className="w-3 h-3 text-slate-400 dark:text-slate-500" />
                            {b.customerPhone}
                          </span>
                        )}
                      </div>

                      <div className="bg-slate-50 dark:bg-slate-950/60 p-3 rounded-lg border border-slate-100 dark:border-slate-800 text-xs text-slate-700 dark:text-slate-300 leading-relaxed">
                        <span className="font-semibold text-slate-800 dark:text-slate-200 block mb-0.5">Client Complaint:</span>
                        "{b.problemDescription}"
                      </div>

                      <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500 dark:text-slate-400">
                        <span className="flex items-center gap-1">
                          <Calendar className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500" /> {b.scheduledDate}
                        </span>
                        <span className="flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500" /> {b.timeSlot}
                        </span>
                        {b.address && (
                          <span className="flex items-center gap-1">
                            <MapPin className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500" /> {b.address.street}, {b.address.city}
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Financials & Actions */}
                    <div className="lg:text-right border-t lg:border-t-0 pt-4 lg:pt-0 border-slate-100 dark:border-slate-800 flex flex-row lg:flex-col justify-between items-end gap-3 flex-shrink-0">
                      <div>
                        <div className="text-[11px] text-slate-500 dark:text-slate-400 font-medium">Diagnostic Fee</div>
                        <div className="text-lg font-bold text-slate-900 dark:text-slate-100 mt-0.5">₹{b.inspectionFee}</div>
                        {b.finalAmount && (
                          <div className="text-xs font-semibold text-teal-800 dark:text-teal-400 mt-0.5">
                            Final Invoiced: ₹{b.finalAmount}
                          </div>
                        )}
                      </div>

                      <div className="flex items-center gap-2">
                        {b.status === 'PENDING' && (
                          <>
                            <button
                              type="button"
                              onClick={() => handleUpdateStatus(b.id, 'ACCEPTED', 'Technician confirmed service appointment')}
                              className="px-3.5 py-1.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors flex items-center gap-1 shadow-sm"
                            >
                              <Check className="w-3.5 h-3.5" /> Accept
                            </button>
                            <button
                              type="button"
                              onClick={() => handleUpdateStatus(b.id, 'REJECTED', 'Technician unavailable for this request')}
                              className="px-3 py-1.5 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-lg text-xs font-medium transition-colors"
                            >
                              Decline
                            </button>
                          </>
                        )}

                        {b.status === 'ACCEPTED' && (
                          <button
                            type="button"
                            onClick={() => handleUpdateStatus(b.id, 'IN_PROGRESS', 'Technician arrived at doorstep and commenced inspection')}
                            className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors shadow-sm flex items-center gap-1.5"
                          >
                            <Play className="w-3.5 h-3.5" /> Start On-Site Work
                          </button>
                        )}

                        {b.status === 'IN_PROGRESS' && (
                          <button
                            type="button"
                            onClick={() => {
                              setSelectedBookingToComplete(b);
                              setFinalAmount(b.inspectionFee.toString());
                              setCompleteModalOpen(true);
                            }}
                            className="px-4 py-2 bg-teal-800 hover:bg-teal-900 dark:bg-teal-700 dark:hover:bg-teal-600 text-white rounded-lg text-xs font-medium transition-colors shadow-sm flex items-center gap-1.5"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5" /> Complete &amp; Invoice
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Customer Reviews Section */}
        {reviews.length > 0 && (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 sm:p-7 border border-slate-200 dark:border-slate-800 shadow-sm space-y-4 transition-colors">
            <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">Verified Customer Testimonials</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {reviews.map((r) => (
                <div key={r.id} className="p-4 bg-slate-50/70 dark:bg-slate-800/50 rounded-lg border border-slate-100 dark:border-slate-800 text-xs">
                  <div className="flex items-center justify-between mb-1.5">
                    <span className="font-semibold text-slate-900 dark:text-slate-100">{r.customerName}</span>
                    <StarRating rating={r.rating} size={13} />
                  </div>
                  <p className="text-slate-600 dark:text-slate-400 leading-relaxed">"{r.comment}"</p>
                  <div className="text-[10px] text-slate-400 dark:text-slate-500 mt-2">
                    {new Date(r.createdAt).toLocaleDateString(undefined, {
                      year: 'numeric',
                      month: 'short',
                      day: 'numeric'
                    })}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Complete & Invoice Modal */}
        {completeModalOpen && selectedBookingToComplete && (
          <div className="fixed inset-0 z-50 bg-slate-950/40 dark:bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="bg-white dark:bg-slate-900 rounded-xl max-w-md w-full p-6 shadow-xl border border-slate-200 dark:border-slate-800 transition-colors">
              <div className="flex items-start justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
                <div>
                  <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">Complete Job &amp; Invoice</h3>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                    Booking #{selectedBookingToComplete.bookingReference} for {selectedBookingToComplete.customerName}
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setCompleteModalOpen(false)}
                  className="p-1 rounded-md text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              <form onSubmit={handleCompleteSubmit} className="mt-4 space-y-4">
                <div>
                  <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                    Final Invoice Amount (₹) <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={finalAmount}
                    onChange={(e) => setFinalAmount(e.target.value)}
                    placeholder="e.g. 1850.00"
                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-lg text-xs text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                  />
                  <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">
                    Base inspection charge: ₹{selectedBookingToComplete.inspectionFee} (include additional parts &amp; labor if applicable)
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                    Repair Summary / Parts Replaced
                  </label>
                  <textarea
                    rows={3}
                    value={completionRemarks}
                    onChange={(e) => setCompletionRemarks(e.target.value)}
                    placeholder="e.g. Replaced internal fuse, calibrated heating element, tested all cycles."
                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-lg text-xs text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                  />
                </div>

                <div className="flex justify-end gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setCompleteModalOpen(false)}
                    className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={submittingComplete || !finalAmount}
                    className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors disabled:opacity-50"
                  >
                    {submittingComplete ? 'Submitting...' : 'Confirm Job Completion'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
