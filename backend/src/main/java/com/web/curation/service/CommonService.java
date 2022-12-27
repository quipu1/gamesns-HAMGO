package com.web.curation.service;

import com.web.curation.model.common.MetadataResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class CommonService {

    public Optional<MetadataResponse> getMetaData(String url) {
        MetadataResponse metadataResponse = null;

        try {
            Document doc = Jsoup.connect(url).get();

            String title = doc.select("meta[property=og:title]").first().attr("content");
            String description = doc.select("meta[property=og:description]").get(0).attr("content");
            String imageUrl = doc.select("meta[property=og:image]").get(0).attr("content");
            String getUrl = doc.select("meta[property=og:url]").get(0).attr("content");

            metadataResponse = new MetadataResponse();

            metadataResponse.setTitle(title);
            metadataResponse.setDesc(description);
            metadataResponse.setImg(imageUrl);
            metadataResponse.setUrl(getUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(metadataResponse);
    }
}
