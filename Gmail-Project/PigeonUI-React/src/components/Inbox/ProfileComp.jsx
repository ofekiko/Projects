import { useNavigate } from 'react-router-dom';
import { DynamicAvatar, logoutFromServer } from './utils/UserFunctions';
import { MdLogout } from 'react-icons/md';

// Profile popup component that allows the user to view their info and log out.
function ProfileComp({ userMail, image, userName, gender, setShowProfile }) {
    // Initialize navigation hook for redirecting the user.
    const navigate = useNavigate(); 

    // Function that logout the user from the inbox page.
    const handleLogout = async () => {
        await logoutFromServer();
        localStorage.removeItem('token');
        setShowProfile(false);
        navigate('/login', { replace: true });
    };

    return (
        <div className="profile-popup">
            {/* Close button to hide the profile panel */}
            <button
                className="close-btn"
                onClick={() => setShowProfile(false)}
            >
                ×
            </button>

            {/* Display user email */}
            <p className="userMail">{userMail}</p>

            {/* Display profile picture or dynamic avatar */}
            {image ? (
                <img src={image} alt="User" className="user-avatar" />
            ) : (
                <DynamicAvatar name={userName} gender={gender} />
            )}

            {/* Hi Message */}
            <p className="hi-message">Hi, {userName}!</p>

            {/* Button container for Logout */}
            <div className="button-container">
                <button
                    className="logout-btn"
                    onClick={handleLogout}
                >
                    <MdLogout size={20} />
                    <span className="sign-out">Sign Out</span>
                </button>
            </div>
        </div>
    );
}

export default ProfileComp;
