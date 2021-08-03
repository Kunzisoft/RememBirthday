package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

/**
 * Created by joker on 08/08/17.
 */

public abstract class AbstractLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    protected Context context;

    protected Uri uri;
    protected String[] projection;
    protected String selection;
    protected String[] selectionArgs;
    protected String sortOrder;

    public AbstractLoader(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                context,
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }
}
