package uk.ptr.cloudinary.tags;

import de.hybris.platform.mediaconversion.web.tag.ImageTag;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;


public class CloudinaryImageTag extends AbstractCloudinaryUrlTag
{
    private static final Logger LOG = Logger.getLogger(CloudinaryImageTag.class);
    private String cssClass;
    private String style;
    private String alt;
    private String title;
    private String id;
    private CloudinaryImageTag.Align align;
    private String border;
    private boolean ismap;
    private String longdesc;
    private String name;
    private String vspace;
    private String usemap;
    private String width;
    private String height;

    public CloudinaryImageTag() {
    }

    public void doTag() throws JspException, IOException
    {
        this.print("<img ");
        this.attribute("src", this.retrieveURL());
        this.attribute("class", this.getCssClass());
        this.attribute("style", this.getStyle());
        this.attribute("width", this.getWidth());
        this.attribute("height", this.getHeight());
        this.attribute("alt", this.getAlt());
        this.attribute("title", this.getTitle());
        this.attribute("id", this.getId());
        this.attribute("align", this.getAlign());
        this.attribute("border", this.getBorder());
        if (this.isIsmap()) {
            this.attribute("ismap", "ismap");
        }

        this.attribute("longdesc", this.getLongdesc());
        this.attribute("name", this.getName());
        this.attribute("vspace", this.getVspace());
        this.attribute("usemap", this.getUsemap());
        this.print("/>");
    }

    private void attribute(String attName, Object value) throws IOException {
        if (value != null) {
            JspWriter out = this.getJspContext().getOut();
            out.print(attName);
            out.print("=\"");
            out.print(value);
            out.print("\" ");
        }

    }

    private void print(String text) throws IOException {
        this.getJspContext().getOut().print(text);
    }

    public String getCssClass() {
        return this.cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CloudinaryImageTag.Align getAlign() {
        return this.align;
    }

    public void setAlign(String align) {
        this.align = CloudinaryImageTag.Align.valueOf(align);
    }

    public String getBorder() {
        return this.border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public boolean isIsmap() {
        return this.ismap;
    }

    public void setIsmap(boolean ismap) {
        this.ismap = ismap;
    }

    public String getLongdesc() {
        return this.longdesc;
    }

    public void setLongdesc(String longdesc) {
        this.longdesc = longdesc;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVspace() {
        return this.vspace;
    }

    public void setVspace(String vspace) {
        this.vspace = vspace;
    }

    public String getUsemap() {
        return this.usemap;
    }

    public void setUsemap(String usemap) {
        this.usemap = usemap;
    }

    static enum Align {
        top,
        middle,
        bottom,
        left,
        right;

        private Align() {
        }
    }
}
