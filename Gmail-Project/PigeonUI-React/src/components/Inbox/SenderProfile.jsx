import { DynamicAvatar } from './utils/UserFunctions';

// Component that displays the sender's profile information: picture, name, email
function SenderProfile({ user }) {
  if (!user) return null;

  return (
    <div className="sender-profile-popup">
      {user.image ? (
        <img src={user.image} alt="Sender" className="sender-avatar" />
      ) : (
        <DynamicAvatar
          name={user.firstName || user.username}
          gender={user.gender}
          className="sender-avatar"
        />
      )}

      <div className="sender-profile-fullname">
        {user.firstName} {user.lastName}
      </div>

      <div className="sender-profile-email">{user.email}</div>
    </div>
  );
}

export default SenderProfile;
