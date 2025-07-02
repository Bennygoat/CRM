// tailwind.config.js
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        logo: {
          lightBlue: '#9DCCF3',
          blue: '#6DA1CD',
          tan: '#DDB675',
        },
      },
      cursor: {
        icepop: "url('/cursors/cursor04.png') 0 0, auto",
      },
      keyframes: {
        scrollRight: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(-50%)' },
        },
        scrollLeft: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(50%)' },
        },
        typing: {
          'from': { width: '0' },
          'to': { width: '100%' },
        },
        blink: {
          '0%, 100%': { borderColor: 'transparent' },
          '50%': { borderColor: '#DDB675' },
        },
      },
      animation: {
        'scroll-right': 'scrollRight 20s linear infinite',
        'scroll-left': 'scrollLeft 20s linear infinite',
        'typing': 'typing 2s steps(30, end) forwards',
        'blink': 'blink 0.75s step-end infinite',
      },
    },
    container: {
      center: true,
      padding: {
        DEFAULT: '1rem',
        sm: '1.5rem',
        lg: '2rem',
        xl: '2.5rem',
        '2xl': '4rem',
      },
      screens: {
        sm: '640px',
        md: '768px',
        lg: '1024px',
        xl: '1280px',
        '2xl': '1536px',
      },
    },
  },
  plugins: [],
};
