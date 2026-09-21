import React from 'react';
import { Link } from 'react-router-dom';
import { Wrench } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-slate-900 dark:bg-slate-950 text-slate-400 py-12 border-t border-slate-800 transition-colors duration-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 md:grid-cols-5 gap-8 mb-12">
          
          {/* Brand Column */}
          <div className="col-span-2">
            <div className="flex items-center gap-2 font-bold text-lg text-white mb-3">
              <div className="w-7 h-7 rounded-md bg-slate-800 dark:bg-slate-800 text-accent-400 flex items-center justify-center">
                <Wrench className="w-3.5 h-3.5" />
              </div>
              <span>Repair<span className="text-accent-400">Match</span></span>
            </div>
            <p className="text-xs text-slate-400 max-w-sm leading-relaxed mb-4">
              A verified repair marketplace connecting customers with technicians based on specific expertise, brand experience, and transparent pricing.
            </p>
            <div className="text-[11px] text-slate-500">
              Reliable local service dispatch across 16 household categories.
            </div>
          </div>

          {/* Services Column */}
          <div>
            <h4 className="text-xs font-semibold text-slate-200 uppercase tracking-wider mb-3">Services</h4>
            <ul className="text-xs space-y-2">
              <li><Link to="/wizard?category=cat-01" className="hover:text-slate-200 transition-colors">Smartphones</Link></li>
              <li><Link to="/wizard?category=cat-02" className="hover:text-slate-200 transition-colors">Laptops &amp; Computers</Link></li>
              <li><Link to="/wizard?category=cat-05" className="hover:text-slate-200 transition-colors">Air Conditioners</Link></li>
              <li><Link to="/wizard?category=cat-14" className="hover:text-slate-200 transition-colors">Plumbing</Link></li>
              <li><Link to="/wizard?category=cat-13" className="hover:text-slate-200 transition-colors">Electrical Repair</Link></li>
            </ul>
          </div>

          {/* For Customers & Technicians */}
          <div>
            <h4 className="text-xs font-semibold text-slate-200 uppercase tracking-wider mb-3">Marketplace</h4>
            <ul className="text-xs space-y-2">
              <li><Link to="/wizard" className="hover:text-slate-200 transition-colors">Book a Repair</Link></li>
              <li><Link to="/customer/bookings" className="hover:text-slate-200 transition-colors">My Bookings</Link></li>
              <li><Link to="/register?role=TECHNICIAN" className="hover:text-slate-200 transition-colors">For Technicians</Link></li>
              <li><Link to="/technician/dashboard" className="hover:text-slate-200 transition-colors">Technician Portal</Link></li>
            </ul>
          </div>

          {/* Company & Legal */}
          <div>
            <h4 className="text-xs font-semibold text-slate-200 uppercase tracking-wider mb-3">Company</h4>
            <ul className="text-xs space-y-2">
              <li><a href="#about" className="hover:text-slate-200 transition-colors">About</a></li>
              <li><a href="#contact" className="hover:text-slate-200 transition-colors">Contact</a></li>
              <li><a href="#terms" className="hover:text-slate-200 transition-colors">Terms of Service</a></li>
              <li><a href="#privacy" className="hover:text-slate-200 transition-colors">Privacy Policy</a></li>
            </ul>
          </div>

        </div>

        {/* Bottom Bar */}
        <div className="pt-6 border-t border-slate-800 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500">
          <p>&copy; {new Date().getFullYear()} RepairMatch. All rights reserved.</p>
          <div className="flex items-center gap-4 mt-2 sm:mt-0">
            <span>Verified technician network</span>
            <span>•</span>
            <span>Transparent pricing</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
