package uk.ptr.cloudinary.tags;


import javax.servlet.jsp.PageContext;


public enum CloudinaryScope {
        page {
            void set(PageContext ctx, String varName, Object value) {
                ctx.setAttribute(varName, value);
            }
        },
        request {
            void set(PageContext ctx, String varName, Object value) {
                ctx.getRequest().setAttribute(varName, value);
            }
        },
        session {
            void set(PageContext ctx, String varName, Object value) {
                ctx.getSession().setAttribute(varName, value);
            }
        },
        application {
            void set(PageContext ctx, String varName, Object value) {
                ctx.getServletContext().setAttribute(varName, value);
            }
        };

        private CloudinaryScope() {
        }

        abstract void set(PageContext var1, String var2, Object var3);
    }
