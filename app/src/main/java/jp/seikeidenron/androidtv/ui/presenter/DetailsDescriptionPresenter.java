/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jp.seikeidenron.androidtv.ui.presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.widget.TextView;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.model.Movie;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    private static final String TAG = DetailsDescriptionPresenter.class.getSimpleName();

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie != null) {
            TextView titleTextView, subtitleTextView, bodyTextView;
            titleTextView = viewHolder.getTitle();
            titleTextView.setMaxLines(100);
            titleTextView.setTextSize(28);
            titleTextView.setText(movie.getTitle());

            subtitleTextView = viewHolder.getSubtitle();
            subtitleTextView.setMaxLines(100);
            subtitleTextView.setText(movie.getStudio());

            bodyTextView = viewHolder.getBody();
            bodyTextView.setMaxLines(100);
            bodyTextView.setText(movie.getDescription());
            MLog.i(TAG, "bodyTVinfo: " + bodyTextView.getMaxHeight() + " x " + bodyTextView.getMaxLines());
            bodyTextView.setHeight(400);

            MLog.v(TAG, "onBindDescription" + movie.getTitle());
            MLog.v(TAG, "title: " + movie.getTitle());
            MLog.v(TAG, "subtitle: " + movie.getStudio());
            MLog.v(TAG, "body: " + movie.getDescription());
        }

    }
}
