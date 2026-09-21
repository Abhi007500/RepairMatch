import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../api/client';
import {
  Smartphone, Laptop, Tablet, Tv, Wind, Refrigerator, Disc, Flame,
  Microwave, Droplets, Fan, BatteryCharging, Zap, Wrench, Hammer, Armchair,
  CheckCircle2, ArrowRight, MapPin, Star, ShieldCheck, Clock, Check
} from 'lucide-react';

const ICON_MAP = {
  Smartphone, Laptop, Tablet, Tv, Wind, Refrigerator, Disc, Flame,
  Microwave, Droplets, Fan, BatteryCharging, Zap, Wrench, Hammer, Armchair
};

export default function HomePage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/catalog/categories')
      .then((res) => setCategories(res.data))
      .catch((err) => console.error("Error loading categories", err))
      .finally(() => setLoading(false));
  }, []);

  const handleCategorySelect = (categoryId) => {
    navigate(`/wizard?category=${categoryId}`);
  };

  return (
    <div className="min-h-screen bg-[#fafafa] dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors duration-200">
      
      {/* 1. HERO SECTION (Clean 2-Column Desktop Layout) */}
      <section className="bg-white dark:bg-slate-900 border-b border-slate-200/80 dark:border-slate-800 pt-12 pb-16 lg:py-20 transition-colors duration-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
            
            {/* Left Column: Headline & Action */}
            <div className="lg:col-span-7 max-w-2xl">
              <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 text-xs font-semibold tracking-wider uppercase mb-5">
                <span className="w-1.5 h-1.5 rounded-full bg-accent-700 dark:bg-accent-400"></span>
                REPAIR SERVICES, SIMPLIFIED
              </div>

              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-slate-900 dark:text-slate-100 leading-[1.15] mb-5">
                Reliable repairs, matched to the right technician.
              </h1>

              <p className="text-base sm:text-lg text-slate-600 dark:text-slate-400 leading-relaxed mb-8 max-w-xl">
                Tell us what's wrong. RepairMatch connects you with verified technicians based on expertise, availability, service area and experience.
              </p>

              <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 mb-10">
                <Link
                  to="/wizard"
                  className="px-6 py-3 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-medium rounded-lg shadow-sm transition-colors text-center text-sm flex items-center justify-center gap-2"
                >
                  Book a Repair
                  <ArrowRight className="w-4 h-4 text-slate-300 dark:text-slate-600" />
                </Link>
                <a
                  href="#categories"
                  className="px-6 py-3 bg-white dark:bg-slate-800 hover:bg-slate-50 dark:hover:bg-slate-700/80 text-slate-700 dark:text-slate-200 font-medium rounded-lg border border-slate-300 dark:border-slate-700 transition-colors text-center text-sm"
                >
                  Browse Services
                </a>
              </div>

              {/* Practical value indicators */}
              <div className="pt-6 border-t border-slate-200 dark:border-slate-800 flex flex-wrap items-center gap-6 text-xs text-slate-600 dark:text-slate-400 font-medium">
                <div className="flex items-center gap-1.5">
                  <Check className="w-3.5 h-3.5 text-accent-700 dark:text-accent-400" />
                  <span>Verified credentials &amp; KYC</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <Check className="w-3.5 h-3.5 text-accent-700 dark:text-accent-400" />
                  <span>Upfront inspection fees</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <Check className="w-3.5 h-3.5 text-accent-700 dark:text-accent-400" />
                  <span>Real operating radius</span>
                </div>
              </div>
            </div>

            {/* Right Column: Tasteful Product Match Showcase */}
            <div className="lg:col-span-5">
              <div className="bg-slate-50 dark:bg-slate-950/60 rounded-xl p-5 border border-slate-200/90 dark:border-slate-800 shadow-sm">
                
                {/* Simulated Matching Card Preview */}
                <div className="bg-white dark:bg-slate-900 rounded-lg p-5 border border-slate-200 dark:border-slate-800 shadow-sm mb-3">
                  <div className="flex items-center justify-between pb-3 mb-3 border-b border-slate-100 dark:border-slate-800">
                    <div>
                      <span className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">Sample Match</span>
                      <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100">Smartphone Screen Repair</h4>
                    </div>
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-medium bg-teal-50 dark:bg-teal-950/60 text-teal-800 dark:text-teal-300 border border-teal-200/80 dark:border-teal-800/80">
                      <span className="w-1.5 h-1.5 rounded-full bg-teal-600 dark:bg-teal-400"></span>
                      Strong Match
                    </span>
                  </div>

                  <div className="flex items-start gap-3 mb-4">
                    <div className="w-10 h-10 rounded-lg bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 flex items-center justify-center font-bold text-slate-700 dark:text-slate-300 text-sm">
                      RK
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5">
                        <span className="font-semibold text-sm text-slate-900 dark:text-slate-100">Rajesh Kumar</span>
                        <ShieldCheck className="w-3.5 h-3.5 text-accent-700 dark:text-accent-400" />
                      </div>
                      <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Apple &amp; Samsung Hardware Specialist</p>
                    </div>
                  </div>

                  <div className="space-y-1.5 text-xs text-slate-600 dark:text-slate-400 mb-4 bg-slate-50 dark:bg-slate-950/60 p-2.5 rounded border border-slate-100 dark:border-slate-800">
                    <div className="flex items-center gap-2">
                      <Check className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400 flex-shrink-0" />
                      <span>Certified brand experience (Apple)</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Check className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400 flex-shrink-0" />
                      <span>Screen &amp; digitizer repair expertise</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Check className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400 flex-shrink-0" />
                      <span>3.2 km from customer address</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Check className="w-3.5 h-3.5 text-teal-700 dark:text-teal-400 flex-shrink-0" />
                      <span>Available for scheduled appointment</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-2 text-xs">
                    <div>
                      <span className="text-slate-500 dark:text-slate-400">Inspection Fee:</span>
                      <span className="font-bold text-slate-900 dark:text-slate-100 ml-1">₹299</span>
                    </div>
                    <div className="flex items-center gap-1 text-slate-700 dark:text-slate-300 font-medium">
                      <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                      <span>4.9</span>
                      <span className="text-slate-400 font-normal">(48 reviews)</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between px-1 text-[11px] text-slate-500 dark:text-slate-400">
                  <span>Matches rank automatically by relevance</span>
                  <Link to="/wizard" className="text-accent-700 dark:text-accent-400 font-semibold hover:underline">
                    Try matching &rarr;
                  </Link>
                </div>

              </div>
            </div>

          </div>
        </div>
      </section>

      {/* 2. HOW REPAIRMATCH WORKS */}
      <section id="how-it-works" className="py-16 border-b border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-900 transition-colors duration-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          
          <div className="max-w-xl mb-12">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500 dark:text-slate-400">Simple Process</span>
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900 dark:text-slate-100 mt-1">
              How RepairMatch Works
            </h2>
            <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
              From diagnosis to completion in three clear steps.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            
            {/* Step 1 */}
            <div className="border border-slate-200 dark:border-slate-800 rounded-lg p-6 bg-[#fafafa] dark:bg-slate-950/60">
              <span className="text-xs font-bold font-mono text-slate-400 dark:text-slate-500 tracking-wider">01</span>
              <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 mt-3 mb-2">
                Tell us what's wrong
              </h3>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Select your service category, device brand, model, and the specific symptom you are experiencing.
              </p>
            </div>

            {/* Step 2 */}
            <div className="border border-slate-200 dark:border-slate-800 rounded-lg p-6 bg-[#fafafa] dark:bg-slate-950/60">
              <span className="text-xs font-bold font-mono text-slate-400 dark:text-slate-500 tracking-wider">02</span>
              <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 mt-3 mb-2">
                See suitable technicians
              </h3>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Our engine ranks technicians within your service radius based on brand history, issue capability, and pricing.
              </p>
            </div>

            {/* Step 3 */}
            <div className="border border-slate-200 dark:border-slate-800 rounded-lg p-6 bg-[#fafafa] dark:bg-slate-950/60">
              <span className="text-xs font-bold font-mono text-slate-400 dark:text-slate-500 tracking-wider">03</span>
              <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 mt-3 mb-2">
                Book and track the repair
              </h3>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Confirm an appointment slot, track the repair lifecycle, and submit a verified review once the job is completed.
              </p>
            </div>

          </div>
        </div>
      </section>

      {/* 3. SERVICE CATEGORIES */}
      <section id="categories" className="py-16 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row sm:items-end justify-between mb-10 gap-4">
          <div>
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500 dark:text-slate-400">Service Catalog</span>
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900 dark:text-slate-100 mt-1">
              Service Categories
            </h2>
            <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
              Select a category to start the diagnostic request.
            </p>
          </div>
          <Link 
            to="/wizard" 
            className="text-xs font-semibold text-accent-700 dark:text-accent-400 hover:text-accent-800 dark:hover:text-accent-300 transition-colors inline-flex items-center gap-1"
          >
            Start diagnosis wizard &rarr;
          </Link>
        </div>

        {loading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3.5">
            {[...Array(16)].map((_, i) => (
              <div key={i} className="h-24 bg-slate-200/70 dark:bg-slate-800/60 rounded-lg animate-pulse" />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3.5">
            {categories.map((cat) => {
              const IconComp = ICON_MAP[cat.iconName] || Wrench;
              return (
                <button
                  key={cat.id}
                  onClick={() => handleCategorySelect(cat.id)}
                  className="group p-4 bg-white dark:bg-slate-900 hover:bg-slate-50 dark:hover:bg-slate-800/80 border border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 rounded-lg text-left transition-all flex flex-col justify-between h-28 shadow-[0_1px_2px_rgba(0,0,0,0.02)]"
                >
                  <div className="flex items-center justify-between w-full">
                    <div className="w-8 h-8 rounded bg-slate-100 dark:bg-slate-800 group-hover:bg-slate-200/80 dark:group-hover:bg-slate-700 text-slate-700 dark:text-slate-300 flex items-center justify-center transition-colors">
                      <IconComp className="w-4 h-4" />
                    </div>
                    <ArrowRight className="w-3.5 h-3.5 text-slate-300 dark:text-slate-600 group-hover:text-slate-600 dark:group-hover:text-slate-300 transition-colors opacity-0 group-hover:opacity-100" />
                  </div>
                  <div>
                    <span className="block text-xs font-semibold text-slate-900 dark:text-slate-100 group-hover:text-accent-800 dark:group-hover:text-accent-400 transition-colors">
                      {cat.name}
                    </span>
                    <span className="text-[10px] text-slate-500 dark:text-slate-400 line-clamp-1 mt-0.5">
                      {cat.description || "Diagnose & match"}
                    </span>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </section>

      {/* 4. MATCHING ENGINE SECTION (Core Differentiator) */}
      <section className="py-16 bg-slate-900 dark:bg-slate-950 text-white border-t border-slate-800 transition-colors duration-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          
          <div className="max-w-3xl mb-12">
            <span className="text-xs font-semibold uppercase tracking-wider text-accent-400">Algorithmic Matching</span>
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-white mt-1">
              Matched for the job, not just the category.
            </h2>
            <p className="text-sm text-slate-400 mt-2 max-w-2xl leading-relaxed">
              Standard service directories simply list whoever paid for ad placement. RepairMatch analyzes 8 technical and geographic parameters to find the most capable technician for your exact situation.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            
            <div className="p-5 bg-slate-800/80 dark:bg-slate-900/80 border border-slate-700/80 dark:border-slate-800 rounded-lg">
              <span className="text-xs font-mono font-semibold text-accent-400">01</span>
              <h4 className="text-sm font-semibold text-white mt-2 mb-1">Brand &amp; Model Experience</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Weighted preference for professionals certified or experienced with your specific manufacturer.
              </p>
            </div>

            <div className="p-5 bg-slate-800/80 dark:bg-slate-900/80 border border-slate-700/80 dark:border-slate-800 rounded-lg">
              <span className="text-xs font-mono font-semibold text-accent-400">02</span>
              <h4 className="text-sm font-semibold text-white mt-2 mb-1">Symptom Expertise</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Considers the exact diagnosed problem (e.g. PCB repair, compressor failure, drain blockages).
              </p>
            </div>

            <div className="p-5 bg-slate-800/80 dark:bg-slate-900/80 border border-slate-700/80 dark:border-slate-800 rounded-lg">
              <span className="text-xs font-mono font-semibold text-accent-400">03</span>
              <h4 className="text-sm font-semibold text-white mt-2 mb-1">True Operating Radius</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Calculated via Haversine distance from technician base to customer address—no out-of-reach dispatches.
              </p>
            </div>

            <div className="p-5 bg-slate-800/80 dark:bg-slate-900/80 border border-slate-700/80 dark:border-slate-800 rounded-lg">
              <span className="text-xs font-mono font-semibold text-accent-400">04</span>
              <h4 className="text-sm font-semibold text-white mt-2 mb-1">Conflict-Free Slots</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Active booking calendars are inspected to eliminate double-bookings before technicians are offered.
              </p>
            </div>

          </div>

          {/* Visual Comparison: Traditional vs RepairMatch */}
          <div className="mt-10 p-6 bg-slate-800/50 dark:bg-slate-900/50 border border-slate-700/60 dark:border-slate-800 rounded-xl">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div>
                <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">Traditional Directory</span>
                <p className="text-xs text-slate-300 mt-2 leading-relaxed">
                  Lists generic technicians alphabetically or by ad spend. You call multiple numbers, explain the issue repeatedly, negotiate fees blindly, and hope they have parts for your brand.
                </p>
              </div>
              <div className="border-t md:border-t-0 md:border-l border-slate-700 dark:border-slate-800 md:pl-8 pt-4 md:pt-0">
                <span className="text-[11px] font-semibold uppercase tracking-wider text-accent-400">RepairMatch</span>
                <p className="text-xs text-slate-300 mt-2 leading-relaxed">
                  Structured problem diagnosis pairs you with verified technicians filtered by actual brand competency, verified inspection rates, and live availability.
                </p>
              </div>
            </div>
          </div>

        </div>
      </section>

      {/* 5. WHY REPAIRMATCH (Product Advantages) */}
      <section className="py-16 bg-white dark:bg-slate-900 border-b border-slate-200/80 dark:border-slate-800 transition-colors duration-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          
          <div className="max-w-xl mb-12">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500 dark:text-slate-400">Platform Standards</span>
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900 dark:text-slate-100 mt-1">
              Built on transparency and verification.
            </h2>
            <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
              A marketplace designed around service quality, clear pricing, and accountability.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            
            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">Verified Technicians</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Technician profiles undergo government ID verification and document review before approval for customer dispatches.
              </p>
            </div>

            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">Transparent Inspection Fees</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Technicians disclose their base diagnostic fee upfront so there are no surprise visit charges.
              </p>
            </div>

            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">Service History &amp; Reviews</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Only customers with completed bookings can submit ratings. Technicians cannot buy or fake review scores.
              </p>
            </div>

            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">Deterministic State Machine</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Both parties track the repair status step-by-step from Acceptance to Completion with full timestamp history.
              </p>
            </div>

            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">Real Operating Radius</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Technicians set their maximum travel radius in kilometers, avoiding dispatch delays or cancelations.
              </p>
            </div>

            <div className="p-5 border border-slate-200 dark:border-slate-800 rounded-lg bg-[#fafafa] dark:bg-slate-950/50">
              <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100 mb-1.5">16 Everyday Categories</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                From smartphones and laptops to plumbing and carpentry—all covered under a single unified marketplace.
              </p>
            </div>

          </div>
        </div>
      </section>

      {/* 6. TECHNICIAN CTA SECTION */}
      <section className="py-16 bg-[#fafafa] dark:bg-slate-950 transition-colors duration-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-8 sm:p-12 rounded-xl shadow-sm">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500 dark:text-slate-400">For Repair Specialists</span>
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900 dark:text-slate-100 mt-2 mb-3">
              Are you a repair professional?
            </h2>
            <p className="text-sm text-slate-600 dark:text-slate-400 max-w-lg mx-auto mb-8 leading-relaxed">
              Join RepairMatch and connect with customers looking for your expertise. Set your operating radius, manage your schedule, and grow your local client base.
            </p>
            <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
              <Link
                to="/register?role=TECHNICIAN"
                className="w-full sm:w-auto px-6 py-2.5 bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 text-xs font-bold rounded-lg shadow-sm transition-colors"
              >
                Join as a Technician
              </Link>
              <Link
                to="/login"
                className="w-full sm:w-auto px-6 py-2.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 text-xs font-semibold rounded-lg transition-colors"
              >
                Sign In to Technician Portal
              </Link>
            </div>
          </div>
        </div>
      </section>

    </div>
  );
}
