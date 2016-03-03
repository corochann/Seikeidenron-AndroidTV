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

package jp.seikeidenron.androidtv.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *  Zasshi class is used for Denshi zasshi.
 *  It holds title, description, image thumbs and video url.
 */
public class Zasshi implements Parcelable {

    private static final String TAG = Zasshi.class.getSimpleName();

    private long   id;
    private String title;
    private String studio;
    private String description;
    private String cardImageUrl;
    private String bgImageUrl;
    private String zasshiUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public Zasshi() {
    }

    public Zasshi(long id, String title, String studio, String description, String bgImageUrl, String cardImageUrl, String zasshiUrl) {
        this.id = id;
        this.title = title;
        this.studio = studio;
        this.description = description;
        this.bgImageUrl = bgImageUrl;
        this.cardImageUrl = cardImageUrl;
        this.zasshiUrl = zasshiUrl;
    }

    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String getZasshiUrl() {
        return zasshiUrl;
    }

    public void setZasshiUrl(String zasshiUrl) {
        this.zasshiUrl = zasshiUrl;
    }

    @Override
    public String toString() {
        return "Zasshi{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", studio='" + studio + '\'' +
                ", description='" + description + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", zasshiUrl='" + zasshiUrl + '\'' +
                ", bgImageUrl='" + bgImageUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.studio);
        dest.writeString(this.description);
        dest.writeString(this.bgImageUrl);
        dest.writeString(this.cardImageUrl);
        dest.writeString(this.zasshiUrl);
    }

    protected Zasshi(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.studio = in.readString();
        this.description = in.readString();
        this.bgImageUrl = in.readString();
        this.cardImageUrl = in.readString();
        this.zasshiUrl = in.readString();
    }

    public static final Creator<Zasshi> CREATOR = new Creator<Zasshi>() {
        public Zasshi createFromParcel(Parcel source) {
            return new Zasshi(source);
        }

        public Zasshi[] newArray(int size) {
            return new Zasshi[size];
        }
    };
}