import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { Wrench, LogOut, Shield, Calendar, Sun, Moon } from 'lucide-react';
import Badge from './Badge';

export default function Navbar() {
  const { user, role, isAuthenticated, logout } = useAuth();
  const { theme, toggleTheme, setMode, isDark } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleServicesClick = (e) => {
    if (location.pathname === '/') {
      e.preventDefault();
      const el = document.getElementById('categories');
      if (el) el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const handleHowItWorksClick = (e) => {
    if (location.pathname === '/') {
      e.preventDefault();
      const el = document.getElementById('how-it-works');
      if (el) el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <header className="sticky top-0 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur-sm border-b border-slate-200/80 dark:border-slate-800 shadow-[0_1px_2px_rgba(0,0,0,0.03)] transition-colors duration-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        
        {/* Brand Logo */}
        <div className="flex items-center gap-8">
          <Link to="/" className="flex items-center gap-2.5 group">
            <div className="w-8 h-8 rounded-lg bg-slate-900 dark:bg-slate-800 text-white flex items-center justify-center font-bold text-sm shadow-sm group-hover:bg-slate-800 dark:group-hover:bg-slate-700 transition-colors">
              <Wrench className="w-4 h-4 text-accent-400" />
            </div>
            <div className="flex flex-col">
              <span className="font-bold text-lg tracking-tight text-slate-900 dark:text-slate-100 leading-none">
                Repair<span className="text-accent-700 dark:text-accent-400">Match</span>
              </span>
              <span className="text-[10px] text-slate-500 dark:text-slate-400 font-medium tracking-wide leading-tight mt-0.5">
                Verified Repair Network
              </span>
            </div>
          </Link>

          {/* Core Navigation Links */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-600 dark:text-slate-300">
            <Link 
              to="/#categories" 
              onClick={handleServicesClick}
              className="hover:text-slate-900 dark:hover:text-white transition-colors"
            >
              Services
            </Link>
            <Link 
              to="/#how-it-works" 
              onClick={handleHowItWorksClick}
              className="hover:text-slate-900 dark:hover:text-white transition-colors"
            >
              How It Works
            </Link>
            <Link 
              to="/register?role=TECHNICIAN" 
              className="hover:text-slate-900 dark:hover:text-white transition-colors text-slate-600 dark:text-slate-300"
            >
              For Technicians
            </Link>
          </nav>
        </div>

        {/* Right Section: Actions, Mode Switcher & Auth */}
        <div className="flex items-center gap-2 sm:gap-3">
          {/* Segmented Light / Dark Mode Switcher */}
          <div className="flex items-center p-0.5 bg-slate-100 dark:bg-slate-800 rounded-lg border border-slate-200 dark:border-slate-700 text-xs font-medium">
            <button
              type="button"
              onClick={() => setMode('light')}
              title="Switch to Light Mode"
              className={`flex items-center gap-1.5 px-2.5 py-1 rounded-md transition-all ${
                !isDark
                  ? 'bg-white text-slate-900 shadow-sm font-semibold'
                  : 'text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-200'
              }`}
            >
              <Sun className={`w-3.5 h-3.5 ${!isDark ? 'text-amber-500 fill-amber-500' : 'text-slate-400'}`} />
              <span className="hidden sm:inline">Light</span>
            </button>
            <button
              type="button"
              onClick={() => setMode('dark')}
              title="Switch to Dark Mode"
              className={`flex items-center gap-1.5 px-2.5 py-1 rounded-md transition-all ${
                isDark
                  ? 'bg-slate-900 dark:bg-slate-700 text-white shadow-sm font-semibold'
                  : 'text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-200'
              }`}
            >
              <Moon className={`w-3.5 h-3.5 ${isDark ? 'text-slate-200' : 'text-slate-400'}`} />
              <span className="hidden sm:inline">Dark</span>
            </button>
          </div>

          <Link
            to="/wizard"
            className="hidden sm:inline-flex items-center justify-center px-3.5 py-1.5 text-xs font-semibold text-white dark:text-slate-900 bg-slate-900 dark:bg-slate-100 hover:bg-slate-800 dark:hover:bg-white rounded-md transition-colors shadow-sm"
          >
            Book a Repair
          </Link>

          {isAuthenticated ? (
            <div className="flex items-center gap-2 pl-2 border-l border-slate-200 dark:border-slate-800">
              {role === 'CUSTOMER' && (
                <Link 
                  to="/customer/bookings" 
                  className="flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-medium text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-md transition-colors"
                >
                  <Calendar className="w-3.5 h-3.5 text-slate-500 dark:text-slate-400" />
                  <span className="hidden sm:inline">My Bookings</span>
                </Link>
              )}

              {role === 'TECHNICIAN' && (
                <Link 
                  to="/technician/dashboard" 
                  className="flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-medium text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-md transition-colors"
                >
                  <Wrench className="w-3.5 h-3.5 text-accent-700 dark:text-accent-400" />
                  <span className="hidden sm:inline">Technician Portal</span>
                </Link>
              )}

              {role === 'ADMIN' && (
                <Link 
                  to="/admin" 
                  className="flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-medium text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-md transition-colors"
                >
                  <Shield className="w-3.5 h-3.5 text-slate-700 dark:text-slate-300" />
                  <span className="hidden sm:inline">Admin</span>
                </Link>
              )}

              <div className="hidden lg:flex flex-col text-right">
                <span className="text-xs font-semibold text-slate-900 dark:text-slate-100 leading-none">{user?.fullName}</span>
                <span className="text-[10px] text-slate-400 capitalize">{role?.toLowerCase()}</span>
              </div>

              <button
                onClick={handleLogout}
                title="Sign Out"
                className="p-1.5 text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-md transition-colors"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2 pl-2 border-l border-slate-200 dark:border-slate-800">
              <Link
                to="/login"
                className="px-3 py-1.5 text-xs font-medium text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-md transition-colors"
              >
                Log In
              </Link>
              <Link
                to="/register"
                className="px-3 py-1.5 text-xs font-semibold text-accent-800 dark:text-accent-300 bg-accent-50 dark:bg-accent-950/60 hover:bg-accent-100 dark:hover:bg-accent-900/60 border border-accent-200/80 dark:border-accent-800/80 rounded-md transition-colors"
              >
                Sign Up
              </Link>
            </div>
          )}
        </div>

      </div>
    </header>
  );
}
