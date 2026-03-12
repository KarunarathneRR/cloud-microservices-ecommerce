import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getUserById } from '../api';
import { useAuth } from '../AuthContext';

export default function Navbar() {
  const { email, role, token, userId, logout } = useAuth();
  const navigate = useNavigate();
  const [displayName, setDisplayName] = useState('');

  useEffect(() => {
    let isActive = true;

    if (!email || !userId) {
      setDisplayName('');
      return () => {
        isActive = false;
      };
    }

    getUserById(userId, token)
      .then((user) => {
        if (!isActive) {
          return;
        }
        const firstPiece = user?.name?.trim()?.split(/\s+/)?.[0] ?? '';
        setDisplayName(firstPiece);
      })
      .catch(() => {
        if (isActive) {
          setDisplayName('');
        }
      });

    return () => {
      isActive = false;
    };
  }, [email, token, userId]);

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav className="bg-indigo-700 text-white shadow-md">
      <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="text-xl font-bold tracking-tight hover:text-indigo-200 transition-colors">
          E-Commerce
        </Link>

        <div className="flex items-center gap-5 text-sm font-medium">
          {email ? (
            <>
              {role !== 'ADMIN' && (
                <Link to="/payment" className="hover:text-indigo-200 transition-colors">
                  Make Payment
                </Link>
              )}
              <Link to="/payments" className="hover:text-indigo-200 transition-colors">
                {role === 'ADMIN' ? 'Payment History' : 'My Payments'}
              </Link>
              {role === 'ADMIN' && (
                <Link to="/customers" className="hover:text-indigo-200 transition-colors">
                  Manage Customers
                </Link>
              )}
              <Link to="/profile" className="hover:text-indigo-200 transition-colors">
                Profile
              </Link>
              <span className="text-indigo-300 text-xs hidden sm:block">{displayName || email}</span>
              <button
                onClick={handleLogout}
                className="bg-indigo-500 hover:bg-indigo-400 px-3 py-1.5 rounded-md transition-colors"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="hover:text-indigo-200 transition-colors">
                Login
              </Link>
              <Link
                to="/register"
                className="bg-white text-indigo-700 hover:bg-indigo-100 px-3 py-1.5 rounded-md transition-colors"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
