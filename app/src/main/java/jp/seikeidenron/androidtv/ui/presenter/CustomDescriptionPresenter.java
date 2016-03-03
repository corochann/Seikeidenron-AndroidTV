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

import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.seikeidenron.androidtv.R;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.model.Movie;

public class CustomDescriptionPresenter extends Presenter {
    private static final String TAG = CustomDescriptionPresenter.class.getSimpleName();

    public static class ViewHolder extends Presenter.ViewHolder {
        private  TextView mTitle;
        private  TextView mSubtitle;
        private  TextView mBody;

        public ViewHolder(final View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.lb_details_description_title);
            mSubtitle = (TextView) view.findViewById(R.id.lb_details_description_subtitle);
            mBody = (TextView) view.findViewById(R.id.lb_details_description_body);
        }

        public TextView getTitle() {
            return mTitle;
        }

        public TextView getSubtitle() {
            return mSubtitle;
        }

        public TextView getBody() {
            return mBody;
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_details_description, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder vh = (ViewHolder) viewHolder;
        onBindDescription(vh, item);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie != null) {
            TextView titleTextView, subtitleTextView, bodyTextView;
            titleTextView = viewHolder.getTitle();
            subtitleTextView = viewHolder.getSubtitle();
            bodyTextView = viewHolder.getBody();

            titleTextView.setTextSize(18);
            titleTextView.setMaxLines(4);
            titleTextView.setText(movie.getTitle());
            subtitleTextView.setText(movie.getStudio());
            bodyTextView.setText(movie.getDescription());

            MLog.v(TAG, "onBindDescription" + movie.getTitle());
            MLog.v(TAG, "title: " + movie.getTitle());
            MLog.v(TAG, "subtitle: " + movie.getStudio());
            MLog.v(TAG, "body: " + movie.getDescription());
        }

    }
}
