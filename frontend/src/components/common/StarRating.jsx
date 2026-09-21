import React from 'react';
import { Star } from 'lucide-react';

export default function StarRating({ rating = 0, max = 5, interactive = false, onRatingChange, size = 16 }) {
  const stars = [];

  for (let i = 1; i <= max; i++) {
    const filled = i <= rating;
    stars.push(
      <button
        type="button"
        key={i}
        disabled={!interactive}
        onClick={() => interactive && onRatingChange && onRatingChange(i)}
        className={`${interactive ? 'cursor-pointer hover:scale-110 transition-transform' : 'cursor-default'} focus:outline-none`}
      >
        <Star
          size={size}
          className={`${filled ? 'text-amber-400 fill-amber-400' : 'text-slate-300'}`}
        />
      </button>
    );
  }

  return <div className="inline-flex items-center gap-0.5">{stars}</div>;
}
