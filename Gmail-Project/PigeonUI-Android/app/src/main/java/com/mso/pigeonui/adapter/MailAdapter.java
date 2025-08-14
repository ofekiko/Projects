package com.mso.pigeonui.adapter; // Or your preferred adapter package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mso.pigeonui.R;
import com.mso.pigeonui.model.MailEntity;

import java.util.Objects;

// Adapter for displaying a list of MailEntity
public class MailAdapter extends ListAdapter<MailEntity, MailAdapter.MailViewHolder> {
    // Listener for item click events.
    private OnItemClickListener listener;

    // Default constructor
    public MailAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil.ItemCallback for comparing MailEntity items
    private static final DiffUtil.ItemCallback<MailEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<MailEntity>() {
        // Checks if two items are the same
        @Override
        public boolean areItemsTheSame(@NonNull MailEntity oldItem, @NonNull MailEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        // Checks if the contents of two items are the same
        @Override
        public boolean areContentsTheSame(@NonNull MailEntity oldItem, @NonNull MailEntity newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getAuthor(), newItem.getAuthor()) &&
                    Objects.equals(oldItem.getSentAt(), newItem.getSentAt()) &&
                    oldItem.isRead() == newItem.isRead() &&
                    Objects.equals(oldItem.getContent(), newItem.getContent());
        }
    };

    // Called when RecyclerView needs a new ViewHolder
    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_mail, parent, false);
        return new MailViewHolder(itemView);
    }

    // Called by RecyclerView to display the data at the specified position
    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        MailEntity currentMail = getItem(position);
        // Determine the sender's name. Use author field, or construct from first/last name,
        // or fallback to "Unknown Sender".
        String senderName = currentMail.getAuthor();
        if (senderName == null || senderName.trim().isEmpty()) {
            if (currentMail.getAuthorFirstName() != null && !currentMail.getAuthorFirstName().trim().isEmpty()) {
                senderName = currentMail.getAuthorFirstName();
                if (currentMail.getAuthorLastName() != null && !currentMail.getAuthorLastName().trim().isEmpty()) {
                    senderName += " " + currentMail.getAuthorLastName();
                }
            } else {
                senderName = "Unknown Sender"; // Fallback
            }
        }
        holder.textViewAuthor.setText(senderName);

        holder.textViewTitle.setText(currentMail.getTitle() != null ? currentMail.getTitle() : "");

        String content = currentMail.getContent();
        if (content != null && content.length() > 50) {
            holder.textViewContent.setText(content.substring(0, 50) + "...");
        } else if (content != null) {
            holder.textViewContent.setText(content);
        } else {
            holder.textViewContent.setText("");
        }

        holder.textViewDate.setText(currentMail.getSentAt() != null ? currentMail.getSentAt() : ""); // You might want to format this date

        if (!currentMail.isRead()) {
            holder.textViewAuthor.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.textViewTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.textViewAuthor.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.textViewTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    // ViewHolder class for MailEntity items
    class MailViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAuthor;
        private TextView textViewTitle;
        private TextView textViewContent;
        private TextView textViewDate;

        // Constructor for the MailViewHolder
        public MailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewDate = itemView.findViewById(R.id.textViewDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    // Interface for receiving click events on list items
    public interface OnItemClickListener {
        void onItemClick(MailEntity mail);
    }

    // Sets the listener for item click events
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}