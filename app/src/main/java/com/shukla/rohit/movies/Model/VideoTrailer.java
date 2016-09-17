package com.shukla.rohit.movies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thero on 15-09-2016.
 */
public class VideoTrailer {


        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("results")
        @Expose
        public List<VideoTralerDetails> results = new ArrayList<VideoTralerDetails>();
}
