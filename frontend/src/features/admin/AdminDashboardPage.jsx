import React, { useState, useEffect, useMemo } from 'react';
import api from '../../api/client';
import Badge from '../../components/common/Badge';
import {
  Shield, Users, CheckCircle, XCircle, FileText, AlertCircle,
  TrendingUp, Award, Clock, ArrowUpRight, Check, ExternalLink,
  Search, ShieldCheck
} from 'lucide-react';

export default function AdminDashboardPage() {
  const [stats, setStats] = useState(null);
  const [pendingTechs, setPendingTechs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [userFilter, setUserFilter] = useState('ALL');
  const [userSearch, setUserSearch] = useState('');

  const loadAdminData = async () => {
    setLoading(true);
    try {
      const [statsRes, pendingRes, usersRes] = await Promise.all([
        api.get('/admin/stats'),
        api.get('/admin/technicians/pending'),
        api.get('/admin/users')
      ]);
      setStats(statsRes.data);
      setPendingTechs(pendingRes.data);
      setUsers(usersRes.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Error loading administrative data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAdminData();
  }, []);

  const handleVerifyTech = async (techId, status) => {
    try {
      await api.put(`/admin/technicians/${techId}/verify?status=${status}`);
      loadAdminData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update technician verification status');
    }
  };

  const filteredUsers = useMemo(() => {
    return users.filter(u => {
      const matchesFilter = userFilter === 'ALL' || u.role === userFilter;
      const matchesSearch = !userSearch.trim() ||
        u.fullName?.toLowerCase().includes(userSearch.toLowerCase()) ||
        u.email?.toLowerCase().includes(userSearch.toLowerCase());
      return matchesFilter && matchesSearch;
    });
  }, [users, userFilter, userSearch]);

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] p-8 max-w-6xl mx-auto space-y-4">
        <div className="h-28 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 animate-pulse" />
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
      <div className="max-w-6xl mx-auto space-y-8">
        {/* Header */}
        <div>
          <h1 className="text-xl sm:text-2xl font-semibold text-slate-900 dark:text-slate-100">Platform Governance &amp; Administration</h1>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Review technician KYC credentials, inspect platform metrics, and oversee the user registry.
          </p>
        </div>

        {error && (
          <div className="p-4 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-300 text-xs rounded-xl flex items-start gap-2.5">
            <AlertCircle className="w-4 h-4 text-rose-600 dark:text-rose-400 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {/* 4 Key Platform Stats */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-white dark:bg-slate-900 p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Platform Accounts</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              {stats?.totalUsers}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Customers &amp; professionals</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Verified Technicians</div>
            <div className="text-xl sm:text-2xl font-bold text-teal-800 dark:text-teal-400 mt-1">
              {stats?.verifiedTechnicians}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Active in matching pool</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Pending KYC Reviews</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              {stats?.pendingKycApprovals}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Awaiting compliance check</div>
          </div>

          <div className="bg-white dark:bg-slate-900 p-5 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-colors">
            <div className="text-[11px] font-medium text-slate-500 dark:text-slate-400">Total Bookings</div>
            <div className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
              {stats?.totalBookings}
            </div>
            <div className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">Across all 16 domains</div>
          </div>
        </div>

        {/* Pending KYC Approvals Queue */}
        <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden transition-colors">
          <div className="p-5 sm:p-6 border-b border-slate-200 dark:border-slate-800 flex flex-wrap items-center justify-between gap-3">
            <div>
              <h2 className="text-base font-semibold text-slate-900 dark:text-slate-100">Technician Verification Queue</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Technicians remain unmatchable until their government ID and trade credentials are verified.
              </p>
            </div>
            <span className="text-xs font-semibold text-slate-700 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2.5 py-1 rounded">
              {pendingTechs.length} Applications Pending
            </span>
          </div>

          {pendingTechs.length === 0 ? (
            <div className="p-10 text-center text-xs text-slate-400 dark:text-slate-500">
              No pending technician verifications. All registered professionals have been audited.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-700 dark:text-slate-300">
                <thead className="bg-slate-50/70 dark:bg-slate-950/60 border-b border-slate-200 dark:border-slate-800 text-slate-500 dark:text-slate-400 uppercase text-[10px] tracking-wider font-semibold">
                  <tr>
                    <th className="py-3 px-5">Technician</th>
                    <th className="py-3 px-5">Experience</th>
                    <th className="py-3 px-5">Diagnostic Rate</th>
                    <th className="py-3 px-5">KYC Document</th>
                    <th className="py-3 px-5 text-right">Verification Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                  {pendingTechs.map((tech) => (
                    <tr key={tech.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/50 transition-colors">
                      <td className="py-3.5 px-5">
                        <div className="font-semibold text-slate-900 dark:text-slate-100">{tech.name}</div>
                        <div className="text-[11px] text-slate-400 dark:text-slate-500">{tech.email}</div>
                      </td>
                      <td className="py-3.5 px-5 text-slate-600 dark:text-slate-400">
                        {tech.experienceYears} Years
                      </td>
                      <td className="py-3.5 px-5 font-medium text-slate-900 dark:text-slate-100">
                        ₹{tech.baseInspectionFee}
                      </td>
                      <td className="py-3.5 px-5">
                        <a
                          href={tech.kycDocumentUrl || '#'}
                          target="_blank"
                          rel="noreferrer"
                          className="inline-flex items-center gap-1 text-teal-800 dark:text-teal-400 font-medium hover:underline"
                        >
                          <FileText className="w-3.5 h-3.5" />
                          <span>View Credential</span>
                          <ExternalLink className="w-3 h-3 text-slate-400" />
                        </a>
                      </td>
                      <td className="py-3.5 px-5 text-right space-x-2">
                        <button
                          type="button"
                          onClick={() => handleVerifyTech(tech.id, 'VERIFIED')}
                          className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 rounded-lg text-xs font-medium transition-colors shadow-sm"
                        >
                          Approve KYC
                        </button>
                        <button
                          type="button"
                          onClick={() => handleVerifyTech(tech.id, 'REJECTED')}
                          className="px-3 py-1.5 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-lg text-xs font-medium transition-colors"
                        >
                          Reject
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Platform User Registry */}
        <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden transition-colors">
          <div className="p-5 sm:p-6 border-b border-slate-200 dark:border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-base font-semibold text-slate-900 dark:text-slate-100">Platform User Directory</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                Complete registry of customer accounts, service professionals, and administrators.
              </p>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              <div className="relative">
                <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  value={userSearch}
                  onChange={(e) => setUserSearch(e.target.value)}
                  placeholder="Search by name or email..."
                  className="pl-8 pr-3 py-1.5 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 w-48 sm:w-56 focus:outline-none focus:ring-1 focus:ring-slate-900 dark:focus:ring-slate-100 focus:border-slate-900"
                />
              </div>

              <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-0.5 rounded-lg text-xs font-medium text-slate-600 dark:text-slate-300">
                {['ALL', 'CUSTOMER', 'TECHNICIAN', 'ADMIN'].map((tab) => (
                  <button
                    key={tab}
                    onClick={() => setUserFilter(tab)}
                    className={`px-2.5 py-1 rounded-md text-[11px] transition-all ${
                      userFilter === tab
                        ? 'bg-white dark:bg-slate-700 text-slate-900 dark:text-slate-100 shadow-sm font-semibold'
                        : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
                    }`}
                  >
                    {tab === 'ALL' ? 'All' : tab.charAt(0) + tab.slice(1).toLowerCase()}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-700 dark:text-slate-300">
              <thead className="bg-slate-50/70 dark:bg-slate-950/60 border-b border-slate-200 dark:border-slate-800 text-slate-500 dark:text-slate-400 uppercase text-[10px] tracking-wider font-semibold">
                <tr>
                  <th className="py-3 px-5">Name &amp; ID</th>
                  <th className="py-3 px-5">Email</th>
                  <th className="py-3 px-5">Phone</th>
                  <th className="py-3 px-5">Account Role</th>
                  <th className="py-3 px-5 text-right">Account Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredUsers.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="py-8 text-center text-slate-400 dark:text-slate-500">
                      No users match the search criteria.
                    </td>
                  </tr>
                ) : (
                  filteredUsers.map((u) => (
                    <tr key={u.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/50 transition-colors">
                      <td className="py-3.5 px-5 font-semibold text-slate-900 dark:text-slate-100">
                        {u.fullName}
                      </td>
                      <td className="py-3.5 px-5 text-slate-600 dark:text-slate-400 font-mono text-[11px]">
                        {u.email}
                      </td>
                      <td className="py-3.5 px-5 text-slate-500 dark:text-slate-400">
                        {u.phoneNumber || '—'}
                      </td>
                      <td className="py-3.5 px-5">
                        <Badge status={u.role} text={u.role} />
                      </td>
                      <td className="py-3.5 px-5 text-right">
                        <span className="inline-flex items-center gap-1.5 text-teal-800 dark:text-teal-400 font-medium text-[11px]">
                          <span className="w-1.5 h-1.5 rounded-full bg-teal-600 dark:bg-teal-400" /> Active
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
