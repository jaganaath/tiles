/**
 * 
 */
package org.apache.tiles.velocity.template;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;
import org.apache.tiles.template.GetAsStringModel;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.Renderable;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link GetAsStringVModel}.
 */
public class GetAsStringVModelTest {
    
    /**
     * The attribute key that will be used to store the parameter map, to use across Velocity tool calls.
     */
    private final static String PARAMETER_MAP_STACK_KEY = "org.apache.tiles.velocity.PARAMETER_MAP_STACK"; 

    /**
     * The model to test.
     */
    private GetAsStringVModel model;
    
    /**
     * The template model.
     */
    private GetAsStringModel tModel;
    
    /**
     * The servlet context.
     */
    private ServletContext servletContext;

    /**
     * The attribute value.
     */
    private Attribute attribute;
    
    /**
     * Sets up the model to test.
     */
    @Before
    public void setUp() {
        tModel = createMock(GetAsStringModel.class);
        servletContext = createMock(ServletContext.class);
        attribute = new Attribute("myAttributeValue");
    }

    /**
     * Test method for {@link org.apache.tiles.velocity.template.GetAsStringVModel#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context, java.util.Map)}.
     * @throws IOException If something goes wrong.
     * @throws ResourceNotFoundException If something goes wrong.
     * @throws ParseErrorException If something goes wrong.
     * @throws MethodInvocationException If something goes wrong.
     */
    @Test
    public void testExecute() throws MethodInvocationException, ParseErrorException, ResourceNotFoundException, IOException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        Context velocityContext = createMock(Context.class);
        TilesContainer container = createMock(TilesContainer.class);
        InternalContextAdapter internalContextAdapter = createMock(InternalContextAdapter.class);
        Writer writer = new StringWriter();
        attribute = new Attribute("myAttributeValue");
        Map<String, Object> params = createParams();
        
        expect(request.getAttribute(ServletUtil.CURRENT_CONTAINER_ATTRIBUTE_NAME)).andReturn(container);
        tModel.execute(container, writer, false, "myPreparer", "myRole", "myDefaultValue",
                "myDefaultValueRole", "myDefaultValueType", "myName", attribute, 
                velocityContext, request, response, writer);
        
        replay(tModel, servletContext, request, response, velocityContext, container, internalContextAdapter);
        initializeModel();
        Renderable renderable = model.execute(request, response, velocityContext, params);
        renderable.render(internalContextAdapter, writer);
        verify(tModel, servletContext, request, response, velocityContext, container, internalContextAdapter);
    }

    /**
     * Test method for {@link org.apache.tiles.velocity.template.GetAsStringVModel#start(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context, java.util.Map)}.
     */
    @Test
    public void testStart() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        Context velocityContext = createMock(Context.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> params = createParams();
        Stack<Object> composeStack = new Stack<Object>();
        Stack<Map<String, Object>> paramStack = new Stack<Map<String,Object>>();

        expect(request.getAttribute(ServletUtil.COMPOSE_STACK_ATTRIBUTE_NAME))
                .andReturn(composeStack);
        expect(request.getAttribute(ServletUtil.CURRENT_CONTAINER_ATTRIBUTE_NAME)).andReturn(container);
        expect(velocityContext.get(PARAMETER_MAP_STACK_KEY)).andReturn(paramStack);
        tModel.start(composeStack, container, false, "myPreparer", "myRole", "myDefaultValue",
                "myDefaultValueRole", "myDefaultValueType", "myName", attribute,
                velocityContext, request, response);
        
        replay(tModel, servletContext, container, request, response, velocityContext);
        initializeModel();
        model.start(request, response, velocityContext, params);
        assertEquals(1, paramStack.size());
        assertEquals(params, paramStack.peek());
        verify(tModel, servletContext, container, request, response, velocityContext);
    }

    /**
     * Test method for {@link org.apache.tiles.velocity.template.GetAsStringVModel#end(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context)}.
     * @throws IOException If something goes wrong.
     * @throws ResourceNotFoundException If something goes wrong.
     * @throws ParseErrorException If something goes wrong.
     * @throws MethodInvocationException If something goes wrong.
     */
    @Test
    public void testEnd() throws MethodInvocationException, ParseErrorException, ResourceNotFoundException, IOException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        Context velocityContext = createMock(Context.class);
        TilesContainer container = createMock(TilesContainer.class);
        InternalContextAdapter internalContextAdapter = createMock(InternalContextAdapter.class);
        Writer writer = new StringWriter();
        Stack<Object> composeStack = new Stack<Object>();
        Map<String, Object> params = createParams();
        Stack<Map<String, Object>> paramStack = new Stack<Map<String,Object>>();
        paramStack.push(params);
        
        expect(request.getAttribute(ServletUtil.CURRENT_CONTAINER_ATTRIBUTE_NAME)).andReturn(container);
        expect(request.getAttribute(ServletUtil.COMPOSE_STACK_ATTRIBUTE_NAME))
                .andReturn(composeStack);
        expect(velocityContext.get(PARAMETER_MAP_STACK_KEY)).andReturn(paramStack);
        tModel.end(composeStack, container, writer, false, velocityContext, request, response, writer);
        
        replay(tModel, servletContext, request, response, velocityContext, container, internalContextAdapter);
        initializeModel();
        Renderable renderable = model.end(request, response, velocityContext);
        renderable.render(internalContextAdapter, writer);
        assertTrue(paramStack.isEmpty());
        verify(tModel, servletContext, request, response, velocityContext, container, internalContextAdapter);
    }

    /**
     * Initializes the model.
     */
    private void initializeModel() {
        model = new GetAsStringVModel(tModel, servletContext);
    }

    /**
     * Creates the parameters to work with the model.
     * 
     * @return The parameters.
     */
    private Map<String, Object> createParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ignore", false);
        params.put("preparer", "myPreparer");
        params.put("role", "myRole");
        params.put("defaultValue", "myDefaultValue");
        params.put("defaultValueRole", "myDefaultValueRole");
        params.put("defaultValueType", "myDefaultValueType");
        params.put("name", "myName");
        params.put("value", attribute);
        params.put("role", "myRole");
        params.put("extends", "myExtends");
        return params;
    }
}