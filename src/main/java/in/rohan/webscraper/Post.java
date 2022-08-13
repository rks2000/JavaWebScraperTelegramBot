package in.rohan.webscraper;

import java.util.Objects;

public class Post
{
    private final String title;
    private final String url;
    private final String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return getTitle().equals(post.getTitle()) && getUrl().equals(post.getUrl()) && getContent().equals(post.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getUrl(), getContent());
    }

    public Post(String title, String url, String content)
    {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }

    public String getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        return "Post{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * Returns a string that is formatted in a way that can be understood by the users.
     */
    public String toMessageString()
    {
        String result = "";

        result += getTitle();
        result += "\n" + getUrl();
        result += "\n" + getContent();

        return result;
    }
}