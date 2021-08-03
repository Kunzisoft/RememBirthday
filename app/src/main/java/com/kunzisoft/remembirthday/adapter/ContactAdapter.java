package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.utility.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapter linked to contacts with birthday for data feeding
 */
public class ContactAdapter<T extends ContactViewHolder> extends RecyclerView.Adapter<T>{

    private static final String TAG = "ContactBirthdayAdapter";

    public static final int POSITION_UNDEFINED = -1;

    private Context context;
    private OnClickItemContactListener onClickItemContactListener;

    protected Cursor cursor;
    protected int contactIdColIdx, contactLookupColIdx, contactNameColIdx, contactThumbnailImageUriColIdx, contactImageUriColIdx;

    // Only used for specific sort of contacts
    protected List<Contact> listContacts;

    private int positionContactChecked = POSITION_UNDEFINED;
    private Drawable circleBackground;
    private int colorHighlight;
    private int colorPrimary;
    private int colorPrimaryInverse;

    public ContactAdapter(Context context) {
        this.context = context;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Get colors from theme
        TypedValue typedValueHighlight = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.backgroundElement, typedValueHighlight, true);
        colorHighlight = typedValueHighlight.data;

        TypedValue typedValuePrimary = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValuePrimary, true);
        colorPrimary = typedValuePrimary.data;

        // Init color primary inverse
        TypedValue typedValueSecondary = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryInverse, typedValueSecondary, true);
        colorPrimaryInverse = typedValueSecondary.data;

        // Init circle background
        circleBackground = ContextCompat.getDrawable(context, R.drawable.background_circle);
        circleBackground.setColorFilter(colorPrimaryInverse, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Change cursor implementation for retrieving data
     * @param cursor New cursor
     * @return The old cursor
     */
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        this.contactIdColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        this.contactLookupColIdx = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        this.contactNameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        this.contactThumbnailImageUriColIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
        this.contactImageUriColIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
    }

    /**
     * Sort the elements according to a comparator by constructing an intermediate list from the cursor. <br />
     * The cursor must be initialized, so you must call {@link #swapCursor(Cursor)} before using {@link #sortElements(Comparator)} <br />
     * WARNING, may take long time if the list contains many elements
     * @param comparator The comparator to sort the list
     */
    public void sortElements(Comparator<Contact> comparator) {
        // TODO make sort
        listContacts = new LinkedList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listContacts.add(getItemFromCursor(cursor));
            cursor.moveToNext();
        }
        Collections.sort(listContacts, comparator);
    }

    /**
     * Reset the cursor add with {@link #swapCursor(Cursor)}
     */
    public void resetCursor() {
        cursor.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_contacts, parent, false);
        return (T) new ContactViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        Contact currentContact;
        if(listContacts != null) {
            // Get contact from list if specific sort is defined
            currentContact = listContacts.get(position);
        } else {
            // Else getAutoSmsById contact directly from cursor
            cursor.moveToPosition(position);
            currentContact = getItemFromCursor(cursor);
        }

        assignDataToView(holder, currentContact, position);

        if(onClickItemContactListener != null) {
            holder.container.setOnClickListener(new BufferContactClickListener(currentContact, position));
        }
    }

    /**
     * MUST BE REDEFINED
     * Must return a new item based on cursor data
     * @param cursor The cursor at the correct position
     * @return The new item created
     */
    protected Contact getItemFromCursor(Cursor cursor) {
        //TODO getAutoSmsById rawcontact id when click
        Contact contact = new Contact(
                cursor.getLong(contactIdColIdx),
                cursor.getString(contactLookupColIdx),
                cursor.getString(contactNameColIdx));
        // Thumbnail
        String uriThumbnailString = cursor.getString(contactThumbnailImageUriColIdx);
        if(uriThumbnailString!=null && !uriThumbnailString.isEmpty()) {
            contact.setImageThumbnailUri(Uri.parse(uriThumbnailString));
        }
        // Photo
        String uriString = cursor.getString(contactImageUriColIdx);
        if(uriString!=null && !uriString.isEmpty())
            contact.setImageUri(Uri.parse(uriString));
        return contact;
    }

    /**
     * MUST BE REDEFINED <br />
     * Method used to link the ViewHolder to the item data
     * @param holder The ViewHolder
     * @param contact The item
     */
    protected void assignDataToView(final T holder, Contact contact, int position) {

        // Highlight the contact
        if(position == positionContactChecked) {
            holder.container.setBackgroundColor(colorHighlight);
        } else {
            Utility.setBackground(holder.container, null);
        }

        // Assign Icon
        if(contact.containsImage()) {
            // ReInit image and background
            holder.icon.setColorFilter(null);
            Utility.setBackground(holder.icon, null);
            // Load Image and crop circle
            Picasso.with(context).load(contact.getImageThumbnailUri())
                    .into(holder.icon, new Callback() {
                        @Override
                        public void onSuccess() {
                            cropRoundedImageView(holder.icon);
                        }
                        @Override
                        public void onError() {}
                    });
        } else {
            // Colorize content of icon
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person_white_24dp));
            holder.icon.setColorFilter(colorPrimary);
            // Colorize background of icon
            Utility.setBackground(holder.icon, circleBackground);
        }

        // Assign name
        holder.name.setText(contact.getName());
    }

    /**
     * Crop ImageView in parameter for create a circle
     */
    private void cropRoundedImageView(ImageView imageView) {
        Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
        imageDrawable.setCircular(true);
        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(imageDrawable);
    }

    /**
     * Retrieves the listener that handles the event on the click of the item
     * @return The listener
     */
    public OnClickItemContactListener getOnClickItemContactListener() {
        return onClickItemContactListener;
    }

    /**
     * Add click contactBirthday listener to each item
     * @param onClickItemContactListener Listener who defined the `onItemContactClick` method
     */
    public void setOnClickItemContactListener(OnClickItemContactListener onClickItemContactListener) {
        this.onClickItemContactListener = onClickItemContactListener;
    }

    /**
     * Determines which item is highlighted
     * @param position Position
     */
    public void setItemCheckedByPosition(int position) {
        int oldPositionChecked = positionContactChecked;
        if(position <= POSITION_UNDEFINED)
            positionContactChecked = POSITION_UNDEFINED;
        else
            positionContactChecked = position;
        notifyItemChanged(oldPositionChecked);
        if(positionContactChecked != POSITION_UNDEFINED)
            notifyItemChanged(position);
    }

    /**
     * Get the first element in adapter
     * @return First contact
     */
    public Contact getFirst() {
        if(listContacts!= null && !listContacts.isEmpty())
            return listContacts.get(0);
        else if(cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast())
                return getItemFromCursor(cursor);
        }
        return null;
    }

    /**
     * Get position of contact
     * @param contact Contact to search
     * @return Contact found, if not found return POSITION_UNDEFINED
     */
    public int getPosition(Contact contact) {
        if(listContacts!= null && !listContacts.isEmpty())
            return listContacts.indexOf(contact);
        else if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Contact contactCursor = getItemFromCursor(cursor);
                if (contact.equals(contactCursor))
                    return cursor.getPosition();
                cursor.moveToNext();
            }
        }
        return POSITION_UNDEFINED;
    }

    /**
     * Select the contact defined in parameter
     * @param contact Contact to setItemCheckedByPosition
     */
    public void setItemChecked(Contact contact) {
        setItemCheckedByPosition(getPosition(contact));
    }

    @Override
    public int getItemCount() {
        if(cursor!=null)
            return cursor.getCount();
        else
            return 0;
    }

    /**
     * Class manager for add contact and view in listener
     */
    private class BufferContactClickListener implements View.OnClickListener {

        private Contact contact;
        private int position;

        BufferContactClickListener(Contact contact, int position) {
            this.contact = contact;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            onClickItemContactListener.onItemContactClick(view, contact, cursor, position);
        }
    }
}
