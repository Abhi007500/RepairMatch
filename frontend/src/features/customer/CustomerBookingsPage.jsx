import React, { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/client';
import Badge from '../../components/common/Badge';
import StarRating from '../../components/common/StarRating';
import PaymentModal from '../payment/PaymentModal';
import {
  Calendar, Clock, MapPin, Wrench, AlertCircle, CheckCircle2,
  XCircle, MessageSquare, Star, Plus, ShieldCheck, Phone, X, Check,
  ChevronRight, ArrowRight, CreditCard
} from 'lucide-react';

const STATUS_ORDER = ['PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED'];

const STATUS_LABELS = {
  PENDING: 'Requested',
  ACCEPTED: 'Confirmed',
  IN_PROGRESS: 'In Service',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  REJECTED: 'Declined'
};

export default function CustomerBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('ALL');

  // Cancellation Modal
  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [selectedBookingToCancel, setSelectedBookingToCancel] = useState(null);
  const [cancelReason, setCancelReason] = useState('');
  const [cancelSubmitting, setCancelSubmitting] = useState(false);

  // Review Modal
  const [reviewModalOpen, setReviewModalOpen] = useState(false);
  const [selectedBookingToReview, setSelectedBookingToReview] = useState(null);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitting, setReviewSubmitting] = useState(false);

  // Online Payment Modal
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [selectedBookingToPay, setSelectedBookingToPay] = useState(null);

  const loadBookings = () => {
    setLoading(true);
    api.get('/bookings/customer')
      .then((res) => setBookings(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Error loading bookings'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const handleCancelSubmit = async (e) => {
    e.preventDefault();
    if (!selectedBookingToCancel) return;

    setCancelSubmitting(true);
    try {
      await api.post(`/bookings/${selectedBookingToCancel.id}/cancel`, {
        reason: cancelReason || 'Cancelled by customer'
      });
      setCancelModalOpen(false);
      setCancelReason('');
      loadBookings();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to cancel booking');
    } finally {
      setCancelSubmitting(false);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!selectedBookingToReview) return;

    setReviewSubmitting(true);
    try {
      await api.post('/reviews', {
        bookingId: selectedBookingToReview.id,
        rating: reviewRating,
        comment: reviewComment
      });
      setReviewModalOpen(false);
      setReviewComment('');
      setReviewRating(5);
      alert('Review submitted successfully. Thank you!');
      loadBookings();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to submit review');
    } finally {
      setReviewSubmitting(false);
    }
  };

  // Filtered bookings
  const filteredBookings = useMemo(() => {
    if (activeTab === 'ACTIVE') {
      return bookings.filter(b => ['PENDING', 'ACCEPTED', 'IN_PROGRESS'].includes(b.status));
    }
    if (activeTab === 'COMPLETED') {
      return bookings.filter(b => b.status === 'COMPLETED');
    }
    if (activeTab === 'CANCELLED') {
      return bookings.filter(b => ['CANCELLED', 'REJECTED'].includes(b.status));
    }
    return bookings;
  }, [bookings, activeTab]);

  const activeCount = bookings.filter(b => ['PENDING', 'ACCEPTED', 'IN_PROGRESS'].includes(b.status)).length;
  const completedCount = bookings.filter(b => b.status === 'COMPLETED').length;

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 py-10 px-4 sm:px-6 lg:px-8 transition-colors duration-200">
      <div className="max-w-5xl mx-auto space-y-6">
        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">Service Bookings</h1>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
              Monitor active repair visits, review audit timelines, and access service receipts.
            </p>
          </div>
          <Link
            to="/wizard"
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium text-xs rounded-lg shadow-sm self-start sm:self-auto transition-colors"
          >
            <Plus className="w-4 h-4" /> Book a Repair
          </Link>
        </div>

        {/* Tab Filters */}
        <div className="flex items-center gap-1 border-b border-slate-200 dark:border-slate-800 pb-px text-xs">
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
          <button
            onClick={() => setActiveTab('ACTIVE')}
            className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'ACTIVE'
                ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
            }`}
          >
            Active ({activeCount})
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
            onClick={() => setActiveTab('CANCELLED')}
            className={`px-3.5 py-2 font-medium border-b-2 transition-colors ${
              activeTab === 'CANCELLED'
                ? 'border-slate-900 dark:border-slate-100 text-slate-900 dark:text-slate-100 font-semibold'
                : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
            }`}
          >
            Cancelled
          </button>
        </div>

        {error && (
          <div className="p-4 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-300 text-xs rounded-xl flex items-start gap-2.5">
            <AlertCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {loading ? (
          <div className="space-y-4">
            {[...Array(2)].map((_, i) => (
              <div key={i} className="h-44 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 animate-pulse" />
            ))}
          </div>
        ) : filteredBookings.length === 0 ? (
          <div className="bg-white dark:bg-slate-900 rounded-xl p-12 text-center border border-slate-200 dark:border-slate-800 shadow-sm">
            <Wrench className="w-10 h-10 text-slate-300 dark:text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">No Bookings in this View</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 mb-6 max-w-sm mx-auto">
              {activeTab === 'ALL'
                ? 'You have not scheduled any repair services yet.'
                : `There are currently no bookings with status: ${activeTab.toLowerCase()}.`}
            </p>
            <Link
              to="/wizard"
              className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 text-xs font-medium rounded-lg transition-colors"
            >
              Start a Diagnostic Request
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredBookings.map((booking) => {
              const isCancelled = ['CANCELLED', 'REJECTED'].includes(booking.status);
              const currentStepIndex = STATUS_ORDER.indexOf(booking.status);

              return (
                <div
                  key={booking.id}
                  className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm hover:border-slate-300 dark:hover:border-slate-700 transition-all overflow-hidden"
                >
                  {/* Top Bar */}
                  <div className="bg-slate-50/70 dark:bg-slate-950/60 px-5 py-3.5 border-b border-slate-200 dark:border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
                    <div className="flex items-center gap-2.5">
                      <span className="font-mono font-semibold text-slate-900 dark:text-slate-100">
                        {booking.bookingReference}
                      </span>
                      <span className="text-slate-300 dark:text-slate-700">•</span>
                      <Badge status={booking.status} />
                      {booking.paymentStatus === 'PAID' && (
                        <span className="text-[10px] font-semibold text-emerald-800 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/50 border border-emerald-200 dark:border-emerald-800/80 px-2 py-0.5 rounded">
                          Paid
                        </span>
                      )}
                    </div>
                    <div className="text-slate-500 dark:text-slate-400 text-[11px]">
                      Booked on {new Date(booking.createdAt).toLocaleDateString(undefined, {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric'
                      })}
                    </div>
                  </div>

                  {/* Main Grid */}
                  <div className="p-5 sm:p-6">
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                      {/* Column 1 & 2: Service & Schedule Details */}
                      <div className="lg:col-span-2 space-y-4">
                        <div className="flex flex-wrap items-center gap-2">
                          <span className="px-2.5 py-1 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200 text-xs font-semibold rounded">
                            {booking.categoryName}
                          </span>
                          {booking.brandName && (
                            <span className="px-2.5 py-1 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 text-xs font-medium rounded">
                              {booking.brandName} {booking.modelName ? `• ${booking.modelName}` : ''}
                            </span>
                          )}
                          {booking.problemTypeTitle && (
                            <span className="text-xs text-slate-500 dark:text-slate-400">
                              ({booking.problemTypeTitle})
                            </span>
                          )}
                        </div>

                        <div className="bg-slate-50/70 dark:bg-slate-950/60 p-3 rounded-lg border border-slate-100 dark:border-slate-800 text-xs text-slate-700 dark:text-slate-300 leading-relaxed">
                          <span className="font-semibold text-slate-900 dark:text-slate-100 block mb-0.5">Problem Reported:</span>
                          {booking.problemDescription}
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs text-slate-600 dark:text-slate-400 pt-1">
                          <div className="flex items-center gap-2">
                            <Calendar className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500 flex-shrink-0" />
                            <span>{booking.scheduledDate}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Clock className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500 flex-shrink-0" />
                            <span>{booking.timeSlot}</span>
                          </div>
                          {booking.address && (
                            <div className="sm:col-span-2 flex items-start gap-2">
                              <MapPin className="w-3.5 h-3.5 text-slate-400 dark:text-slate-500 flex-shrink-0 mt-0.5" />
                              <span>{booking.address.street}, {booking.address.city}, {booking.address.postalCode}</span>
                            </div>
                          )}
                        </div>
                      </div>

                      {/* Column 3: Technician & Billing Summary */}
                      <div className="bg-slate-50/60 dark:bg-slate-950/40 p-4 rounded-xl border border-slate-200/80 dark:border-slate-800 flex flex-col justify-between space-y-4">
                        <div>
                          <div className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-2">
                            Assigned Technician
                          </div>
                          <div className="font-semibold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                            {booking.technicianName}
                            <ShieldCheck className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400" title="KYC Verified" />
                          </div>
                          {booking.technicianPhone && (
                            <div className="flex items-center gap-1.5 text-xs text-slate-600 dark:text-slate-400 mt-1">
                              <Phone className="w-3 h-3 text-slate-400 dark:text-slate-500" />
                              <span>{booking.technicianPhone}</span>
                            </div>
                          )}

                          <div className="mt-4 pt-3 border-t border-slate-200/70 dark:border-slate-800 space-y-1.5 text-xs">
                            <div className="flex justify-between text-slate-600 dark:text-slate-400">
                              <span>Inspection Fee:</span>
                              <span className="font-semibold text-slate-900 dark:text-slate-100">₹{booking.inspectionFee}</span>
                            </div>
                            {booking.finalAmount && (
                              <div className="flex justify-between text-slate-900 dark:text-slate-100 font-semibold pt-1 border-t border-slate-200/60 dark:border-slate-850">
                                <span>Total Invoice:</span>
                                <span className="text-teal-800 dark:text-teal-400 font-bold">₹{booking.finalAmount}</span>
                              </div>
                            )}
                            <div className="flex justify-between items-center pt-1 border-t border-slate-200/60 dark:border-slate-850">
                              <span className="text-slate-500 dark:text-slate-400">Payment Status:</span>
                              {booking.paymentStatus === 'PAID' ? (
                                <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-teal-700 dark:text-teal-300 bg-teal-50 dark:bg-teal-950/60 px-2 py-0.5 rounded border border-teal-200/60 dark:border-teal-800">
                                  <ShieldCheck className="w-3 h-3 text-teal-600 dark:text-teal-400" /> Paid Online
                                </span>
                              ) : (
                                <span className="text-[11px] font-semibold text-amber-700 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/60 px-2 py-0.5 rounded border border-amber-200/60 dark:border-amber-800">
                                  Payment Pending
                                </span>
                              )}
                            </div>
                          </div>
                        </div>

                        {/* Action Buttons */}
                        <div className="pt-3 border-t border-slate-200/70 dark:border-slate-800 space-y-2">
                          {booking.paymentStatus !== 'PAID' && !['CANCELLED', 'REJECTED'].includes(booking.status) && (
                            <button
                              type="button"
                              onClick={() => {
                                setSelectedBookingToPay(booking);
                                setPaymentModalOpen(true);
                              }}
                              className="w-full py-2 px-3 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 text-xs font-semibold rounded-lg shadow-sm transition-colors flex items-center justify-center gap-1.5"
                            >
                              <CreditCard className="w-3.5 h-3.5" />
                              Pay ₹{booking.finalAmount || booking.inspectionFee} Online
                            </button>
                          )}
                          {['PENDING', 'ACCEPTED'].includes(booking.status) && (
                            <button
                              type="button"
                              onClick={() => {
                                setSelectedBookingToCancel(booking);
                                setCancelModalOpen(true);
                              }}
                              className="w-full py-1.5 px-3 border border-rose-200 dark:border-rose-900 hover:bg-rose-50 dark:hover:bg-rose-950/40 text-rose-700 dark:text-rose-400 text-xs font-medium rounded-lg transition-colors text-center"
                            >
                              Cancel Booking
                            </button>
                          )}

                          {booking.status === 'COMPLETED' && (
                            <button
                              type="button"
                              onClick={() => {
                                setSelectedBookingToReview(booking);
                                setReviewModalOpen(true);
                              }}
                              className="w-full py-2 px-3 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 text-xs font-medium rounded-lg flex items-center justify-center gap-1.5 transition-colors shadow-sm"
                            >
                              <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                              Submit Verified Review
                            </button>
                          )}
                        </div>
                      </div>
                    </div>

                    {/* Service Lifecycle Track */}
                    {!isCancelled && (
                      <div className="mt-6 pt-5 border-t border-slate-100 dark:border-slate-800">
                        <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-3">
                          Service Status Progression
                        </div>
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                          {STATUS_ORDER.map((s, sIdx) => {
                            const isDone = currentStepIndex >= sIdx;
                            const isCurrent = booking.status === s;
                            return (
                              <div
                                key={s}
                                className={`p-2.5 rounded-lg border text-xs ${
                                  isCurrent
                                    ? 'bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 border-slate-900 dark:border-slate-100'
                                    : isDone
                                    ? 'bg-teal-50/50 dark:bg-teal-950/40 text-teal-900 dark:text-teal-300 border-teal-200/70 dark:border-teal-800/60'
                                    : 'bg-slate-50 dark:bg-slate-850/60 text-slate-400 dark:text-slate-500 border-slate-200 dark:border-slate-800'
                                }`}
                              >
                                <div className="flex items-center gap-1.5 font-medium">
                                  {isDone ? (
                                    <Check className={`w-3.5 h-3.5 ${isCurrent ? 'text-white dark:text-slate-900' : 'text-teal-700 dark:text-teal-400'}`} />
                                  ) : (
                                    <div className="w-3 h-3 rounded-full border border-slate-300 dark:border-slate-600" />
                                  )}
                                  <span>{STATUS_LABELS[s]}</span>
                                </div>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    )}

                    {/* Timeline History */}
                    {booking.timeline && booking.timeline.length > 0 && (
                      <div className="mt-5 pt-4 border-t border-slate-100 dark:border-slate-800">
                        <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2.5">
                          Audit Trail Log
                        </div>
                        <div className="space-y-2">
                          {booking.timeline.map((entry, tIdx) => (
                            <div key={entry.id || tIdx} className="flex items-start gap-2.5 text-xs">
                              <div className="w-1.5 h-1.5 rounded-full bg-slate-400 dark:bg-slate-600 mt-1.5 flex-shrink-0" />
                              <div className="flex-1">
                                <span className="font-semibold text-slate-800 dark:text-slate-200">{entry.status}</span>
                                {entry.remarks && (
                                  <span className="text-slate-500 dark:text-slate-400 ml-2">— {entry.remarks}</span>
                                )}
                              </div>
                              <div className="text-[11px] text-slate-400 dark:text-slate-500 flex-shrink-0 font-mono">
                                {new Date(entry.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Cancellation Modal */}
        {cancelModalOpen && selectedBookingToCancel && (
          <div className="fixed inset-0 z-50 bg-slate-950/40 dark:bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="bg-white dark:bg-slate-900 rounded-xl max-w-md w-full p-6 shadow-xl border border-slate-200 dark:border-slate-800 transition-colors">
              <div className="flex items-start justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
                <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">Cancel Service Booking</h3>
                <button
                  type="button"
                  onClick={() => setCancelModalOpen(false)}
                  className="p-1 rounded-md text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              <p className="text-xs text-slate-500 dark:text-slate-400 mt-3 mb-4 leading-relaxed">
                Are you sure you want to cancel booking <strong className="font-mono text-slate-800 dark:text-slate-200">{selectedBookingToCancel.bookingReference}</strong>?
                Your assigned technician will be notified immediately.
              </p>

              <form onSubmit={handleCancelSubmit}>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                  Cancellation Reason <span className="text-rose-500">*</span>
                </label>
                <textarea
                  required
                  rows={3}
                  value={cancelReason}
                  onChange={(e) => setCancelReason(e.target.value)}
                  placeholder="e.g. Schedule conflict, problem resolved independently, etc."
                  className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-lg text-xs text-slate-900 dark:text-slate-100 mb-4 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                />

                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => setCancelModalOpen(false)}
                    className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                  >
                    Keep Booking
                  </button>
                  <button
                    type="submit"
                    disabled={cancelSubmitting || !cancelReason.trim()}
                    className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-lg text-xs font-medium transition-colors disabled:opacity-50"
                  >
                    {cancelSubmitting ? 'Cancelling...' : 'Confirm Cancellation'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Review Modal */}
        {reviewModalOpen && selectedBookingToReview && (
          <div className="fixed inset-0 z-50 bg-slate-950/40 dark:bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="bg-white dark:bg-slate-900 rounded-xl max-w-md w-full p-6 shadow-xl border border-slate-200 dark:border-slate-800 transition-colors">
              <div className="flex items-start justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
                <div>
                  <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">Service Review &amp; Rating</h3>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                    For {selectedBookingToReview.technicianName} ({selectedBookingToReview.categoryName})
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setReviewModalOpen(false)}
                  className="p-1 rounded-md text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              <form onSubmit={handleReviewSubmit} className="mt-4 space-y-4">
                <div className="text-center py-4 bg-slate-50 dark:bg-slate-800/60 rounded-lg border border-slate-100 dark:border-slate-800">
                  <div className="text-xs text-slate-600 dark:text-slate-400 mb-2">Select star rating:</div>
                  <StarRating
                    rating={reviewRating}
                    interactive={true}
                    size={26}
                    onRatingChange={(r) => setReviewRating(r)}
                  />
                  <div className="text-xs font-semibold text-slate-900 dark:text-slate-100 mt-1.5">
                    {reviewRating} out of 5 Stars
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                    Feedback &amp; Experience Details <span className="text-rose-500">*</span>
                  </label>
                  <textarea
                    required
                    rows={3}
                    value={reviewComment}
                    onChange={(e) => setReviewComment(e.target.value)}
                    placeholder="How was the technician's punctuality, diagnostic accuracy, parts quality, and professionalism?"
                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-lg text-xs text-slate-900 dark:text-slate-100 focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                  />
                </div>

                <div className="flex justify-end gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setReviewModalOpen(false)}
                    className="px-4 py-2 border border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 rounded-lg text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={reviewSubmitting || !reviewComment.trim()}
                    className="px-4 py-2 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors disabled:opacity-50"
                  >
                    {reviewSubmitting ? 'Submitting...' : 'Submit Review'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Online Payment Modal */}
        <PaymentModal
          isOpen={paymentModalOpen}
          onClose={() => {
            setPaymentModalOpen(false);
            setSelectedBookingToPay(null);
          }}
          booking={selectedBookingToPay}
          onPaymentSuccess={() => {
            loadBookings();
          }}
        />
      </div>
    </div>
  );
}
