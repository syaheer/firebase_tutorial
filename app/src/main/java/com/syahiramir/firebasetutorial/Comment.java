package com.syahiramir.firebasetutorial;

public class Comment {

    private String name;
    private String comment;
    private String userId;
    private String commentId;

    public Comment() {
    }

    public Comment(String name, String comment, String userId, String commentId) {
        this.name = name;
        this.comment = comment;
        this.userId = userId;
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
