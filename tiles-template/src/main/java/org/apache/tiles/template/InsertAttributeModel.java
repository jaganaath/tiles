package org.apache.tiles.template;

import java.io.IOException;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.Attribute;
import org.apache.tiles.TilesContainer;

public class InsertAttributeModel {

    private Log log = LogFactory.getLog(getClass());

    private AttributeResolver attributeResolver;

    public InsertAttributeModel(AttributeResolver attributeResolver) {
        this.attributeResolver = attributeResolver;
    }

    public void start(Stack<Object> composeStack, TilesContainer container,
            boolean ignore, String preparer, String role, Object defaultValue,
            String defaultValueRole, String defaultValueType, String name,
            Attribute value, Object... requestItems) {
        Attribute attribute = resolveAttribute(container, ignore, preparer,
                role, defaultValue, defaultValueRole, defaultValueType, name,
                value, requestItems);
        composeStack.push(attribute);
    }

    public void end(Stack<Object> composeStack, TilesContainer container,
            boolean ignore, Object... requestItems) throws IOException {
        Attribute attribute = (Attribute) composeStack.pop();
        renderAttribute(container, ignore, attribute, requestItems);
    }

    public void execute(TilesContainer container,
            boolean ignore, String preparer, String role, Object defaultValue,
            String defaultValueRole, String defaultValueType, String name,
            Attribute value, Object... requestItems) throws IOException {
        Attribute attribute = resolveAttribute(container, ignore, preparer,
                role, defaultValue, defaultValueRole, defaultValueType, name,
                value, requestItems);
        renderAttribute(container, ignore, attribute, requestItems);
    }

    private Attribute resolveAttribute(TilesContainer container,
            boolean ignore, String preparer, String role, Object defaultValue,
            String defaultValueRole, String defaultValueType, String name,
            Attribute value, Object... requestItems) {
        if (preparer != null) {
            container.prepare(preparer, requestItems);
        }
        Attribute attribute = attributeResolver.computeAttribute(container,
                value, name, role, ignore, defaultValue,
                defaultValueRole, defaultValueType, requestItems);
        container.startContext(requestItems);
        return attribute;
    }

    private void renderAttribute(TilesContainer container, boolean ignore,
            Attribute attribute, Object... requestItems) throws IOException {
        try {
            if (attribute == null && ignore) {
                return;
            }
            container.render(attribute, requestItems);
        } catch (IOException e) {
            if (!ignore) {
                throw e;
            } else if (log.isDebugEnabled()) {
                log.debug("Ignoring exception", e);
            }
        } catch (RuntimeException e) {
            if (!ignore) {
                throw e;
            } else if (log.isDebugEnabled()) {
                log.debug("Ignoring exception", e);
            }
        } finally {
            container.endContext(requestItems);
        }
    }
}