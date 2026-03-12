import { Link } from 'react-router-dom';
import { useAuth } from '../AuthContext';

export default function HomePage() {
  const { email, role } = useAuth();

  const cards = [
    {
      title: 'Register',
      description: 'Create a new user account with name, email, password and role.',
      to: '/register',
      icon: <UserPlusIcon />,
      cta: 'Sign Up',
      show: !email,
    },
    {
      title: 'Login',
      description: 'Authenticate and receive a JWT token for subsequent requests.',
      to: '/login',
      icon: <LoginIcon />,
      cta: 'Sign In',
      show: !email,
    },
    {
      title: 'My Profile',
      description: 'View your account details including ID, email, and role.',
      to: '/profile',
      icon: <ProfileIcon />,
      cta: 'View Profile',
      show: !!email,
    },
    {
      title: role === 'ADMIN' ? 'Manage Users' : 'Make Payment',
      description:
        role === 'ADMIN'
          ? 'Manage customer accounts and review registered users.'
          : 'Process a payment for an order. User is validated against user-service.',
      to: role === 'ADMIN' ? '/customers' : '/payment',
      icon: role === 'ADMIN' ? <UsersIcon /> : <CardIcon />,
      cta: role === 'ADMIN' ? 'Manage Users' : 'Pay Now',
      show: !!email,
    },
    {
      title: 'Payment History',
      description:
        role === 'ADMIN'
          ? 'Browse the full payment history of all customers with search and filtering.'
          : 'Browse your past payments with status, amount, and transaction details.',
      to: '/payments',
      icon: <HistoryIcon />,
      cta: role === 'ADMIN' ? 'View All Payments' : 'View History',
      show: !!email,
    },
  ].filter((card) => card.show);

  return (
    <div className="max-w-5xl mx-auto px-4 py-12">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-extrabold text-gray-800 mb-3">E-Commerce Platform</h1>
        {email && (
          <div className="mt-4 inline-flex items-center gap-2 bg-indigo-50 border border-indigo-100 text-indigo-700 rounded-full px-4 py-1.5 text-sm font-medium">
            Logged in as <span className="font-bold">{email}</span>
          </div>
        )}
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cards.map((card) => (
          <div
            key={card.to}
            className="bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow p-6 flex flex-col"
          >
            <div className="text-indigo-500 mb-4">{card.icon}</div>
            <h3 className="text-lg font-semibold text-gray-800 mb-2">{card.title}</h3>
            <p className="text-sm text-gray-500 flex-1 mb-4">{card.description}</p>
            <Link
              to={card.to}
              className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg text-center transition-colors"
            >
              {card.cta}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

function UserPlusIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="8.5" cy="7" r="4" />
      <path d="M20 8v6" />
      <path d="M17 11h6" />
    </svg>
  );
}

function LoginIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" />
      <path d="M10 17l5-5-5-5" />
      <path d="M15 12H3" />
    </svg>
  );
}

function ProfileIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <path d="M20 21a8 8 0 1 0-16 0" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  );
}

function CardIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <rect x="2" y="5" width="20" height="14" rx="2" />
      <path d="M2 10h20" />
    </svg>
  );
}

function UsersIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="8.5" cy="7" r="4" />
      <path d="M20 8a4 4 0 0 1 0 8" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
    </svg>
  );
}

function HistoryIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-7 h-7" aria-hidden="true">
      <path d="M3 3v5h5" />
      <path d="M3.05 13A9 9 0 1 0 6 5.3L3 8" />
      <path d="M12 7v5l4 2" />
    </svg>
  );
}

function ServiceInfo({ name, port, color, paths }) {
  return (
    <div className="border border-gray-100 rounded-xl p-4">
      <div className="flex items-center gap-2 mb-3">
        <span className={`w-2.5 h-2.5 rounded-full ${color}`} />
        <span className="font-semibold text-gray-700">{name}</span>
        <span className="ml-auto text-xs text-gray-400 font-mono">:{port}</span>
      </div>
      <ul className="space-y-1">
        {paths.map((path) => (
          <li key={path} className="text-xs text-gray-500 font-mono">
            {path}
          </li>
        ))}
      </ul>
    </div>
  );
}
