import { useEffect, useState, useRef } from 'react';
import { getUser, DynamicAvatar } from './utils/UserFunctions';
import drabukaSound from '../../assets/audio/drabuka.mp3';
import pigeonIcon from '../../images/bird.png';
import '../../style/Inbox/TopBarInbox.css';
import ProfileComp from './ProfileComp';


// The top bar component of the inbox
export default function TopBarInbox({ darkMode, toggleDarkMode }) {

  // State hooks
  const [user, setUser] = useState(null);
  const [showProfile, setShowProfile] = useState(false);
  const pigeonAudioRef = useRef(null);

  // Effect hook to fetch and load the current user's data.
  useEffect(() => {
    const fetchUser = async () => {
      const userId = localStorage.getItem('userId');
      if (userId) {
        try {
          const userData = await getUser(userId);
          console.log('Fetched user data:', userData);
          setUser(userData);
        } catch (err) {
          console.error('Error fetching user:', err);
        }
      }
    };

    fetchUser();
  }, []);

  // When pressing the pigeon icon it start to tweet.
  const handlePigeonHover = () => {
    if (pigeonAudioRef.current) {
      pigeonAudioRef.current.currentTime = 0;
      pigeonAudioRef.current.play();
          setTimeout(() => {
      pigeonAudioRef.current.pause();
      pigeonAudioRef.current.currentTime = 0;
    }, 10000);
    }

    // When pressing the pigeon icon it start to dance.
    const pigeon = document.querySelector('.pigeon-icon');
    if (pigeon) {
      pigeon.classList.add('fly-away');
      setTimeout(() => pigeon.classList.remove('fly-away'), 1200);
    }
  };

  return (
    <div className="top-bar-inbox">
      {/* Welcome note to the site */}
      <div className="top-bar-welcome">
        Good to see you at <strong>PigeonUI</strong>
        <img
          src={pigeonIcon}
          alt="Pigeon"
          className="pigeon-icon"
          onMouseEnter={handlePigeonHover}
        />
        <audio ref={pigeonAudioRef} src={drabukaSound} preload="auto" />
      </div>

      <div className="top-bar-actions">

        {/* Drak mode button */}
        <button
          className="dark-toggle"
          onClick={toggleDarkMode}
          aria-label="Toggle Dark Mode"
        >
          {darkMode ? '☀️' : '🌙'}
        </button>

        {/* Profile icon button - shows user's avatar or dynamic avatar */}
        <button
          className="profile-btn"
          onClick={() => setShowProfile(true)}
        >
          {user?.image ? (
            <img src={user.image} alt="User" className="user-avatar" />
          ) : (
            user && <DynamicAvatar name={user.firstName} gender={user.gender} />
          )}
        </button>
        
      </div>

      {/* Render the profile popup if it's toggled on */}
      {showProfile && (
        <ProfileComp
          userMail={user.email}
          image={user.image}
          userName={user.firstName}
          gender={user.gender}
          setShowProfile={setShowProfile}
        />
      )}
    </div>
  );
}
