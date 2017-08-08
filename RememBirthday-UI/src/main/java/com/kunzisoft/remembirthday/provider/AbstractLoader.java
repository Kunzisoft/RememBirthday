package com.kunzisoft.remembirthday.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
