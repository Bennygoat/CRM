import { useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Footer from './components/Footer';
import Header from './components/Header';
import ScrollToTop from './components/ScorllToTop';
import BackToTop from "./components/YuYu/BackToTop";

export default function App() {
  const location = useLocation();
  const isHome = location.pathname === '/';

  useEffect(() => {
    const frontendPaths = ['/', '/about', '/product', '/store'];
    const isFrontend = frontendPaths.some(path => location.pathname.startsWith(path));

    if (isFrontend) {
      document.body.classList.add('cursor-frontend');
    } else {
      document.body.classList.remove('cursor-frontend');
    }
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-white">
      <ScrollToTop />
      <Header />
      <main className={isHome ? "pt-[0px]" : "pt-[100px]"}>
        <Outlet />
      </main>
      <BackToTop />
      <Footer />
    </div>
  );
}
