import React from 'react';

const BADGE_STYLES = {
  PENDING: {
    bg: 'bg-amber-50 dark:bg-amber-950/40',
    text: 'text-amber-800 dark:text-amber-300',
    border: 'border-amber-200/80 dark:border-amber-800/60',
    dot: 'bg-amber-500'
  },
  ACCEPTED: {
    bg: 'bg-blue-50 dark:bg-blue-950/40',
    text: 'text-blue-800 dark:text-blue-300',
    border: 'border-blue-200/80 dark:border-blue-800/60',
    dot: 'bg-blue-500'
  },
  IN_PROGRESS: {
    bg: 'bg-indigo-50 dark:bg-indigo-950/40',
    text: 'text-indigo-800 dark:text-indigo-300',
    border: 'border-indigo-200/80 dark:border-indigo-800/60',
    dot: 'bg-indigo-500'
  },
  COMPLETED: {
    bg: 'bg-emerald-50 dark:bg-emerald-950/40',
    text: 'text-emerald-800 dark:text-emerald-300',
    border: 'border-emerald-200/80 dark:border-emerald-800/60',
    dot: 'bg-emerald-500'
  },
  CANCELLED: {
    bg: 'bg-slate-100 dark:bg-slate-800/60',
    text: 'text-slate-600 dark:text-slate-400',
    border: 'border-slate-200 dark:border-slate-700',
    dot: 'bg-slate-400'
  },
  REJECTED: {
    bg: 'bg-rose-50 dark:bg-rose-950/40',
    text: 'text-rose-700 dark:text-rose-300',
    border: 'border-rose-200/80 dark:border-rose-800/60',
    dot: 'bg-rose-500'
  },
  VERIFIED: {
    bg: 'bg-teal-50 dark:bg-teal-950/40',
    text: 'text-teal-800 dark:text-teal-300',
    border: 'border-teal-200/80 dark:border-teal-800/60',
    dot: 'bg-teal-500'
  },
  PAID: {
    bg: 'bg-teal-50 dark:bg-teal-950/40',
    text: 'text-teal-800 dark:text-teal-300',
    border: 'border-teal-200/80 dark:border-teal-800/60',
    dot: 'bg-teal-500'
  },
};

const DEFAULT_STYLE = {
  bg: 'bg-slate-100 dark:bg-slate-800/60',
  text: 'text-slate-700 dark:text-slate-300',
  border: 'border-slate-200 dark:border-slate-700',
  dot: 'bg-slate-400'
};

export default function Badge({ status, text, withDot = true }) {
  const normalized = (status || '').toUpperCase();
  const cfg = BADGE_STYLES[normalized] || DEFAULT_STYLE;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded text-[11px] font-medium border ${cfg.bg} ${cfg.text} ${cfg.border} transition-colors`}>
      {withDot && <span className={`w-1.5 h-1.5 rounded-full ${cfg.dot}`} />}
      <span>{text || status}</span>
    </span>
  );
}
