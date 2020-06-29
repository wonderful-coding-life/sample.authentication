package com.sample.authentication;

public class ImgbbResult {
    public boolean success;
    public int status;
    public Data data;

    public class Data {
        public String id;
        public String url_viewer;
        public String url;
        public String time;
        public String expiration;
        public String display_url;
        public Image image;
        public Thumb thumb;
        public Medium medium;
        public String delete_url;
        public String title;

        public class Image {
            public String filename;
            public String name;
            public String mime;
            public String extension;
            public String url;
            public int size;
        }

        public class Thumb {
            public String filename;
            public String name;
            public String mime;
            public String extension;
            public String url;
        }

        public class Medium {
            public String filename;
            public String name;
            public String mime;
            public String extension;
            public String url;
        }
    }
}
