package org.ctavkep.pquest;

public class Product {
    String productName;
    String productDesc;
    String productThumbnail;
    String productScreenshot;
    String productUrl;
    int productComments;
    private int categoryID;
    private int postID;
    int upvotes;

    public Product(String productName, String productDesc, String productThumbnail,
                   String productScreenshot, String productUrl, int productComments,
                   int categoryID, int postID, int upvotes) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productThumbnail = productThumbnail;
        this.productScreenshot = productScreenshot;
        this.productUrl = productUrl;
        this.productComments = productComments;
        this.categoryID = categoryID;
        this.postID = postID;
        this.upvotes = upvotes;
    }

    public int getPostID() {
        return postID;
    }
}
