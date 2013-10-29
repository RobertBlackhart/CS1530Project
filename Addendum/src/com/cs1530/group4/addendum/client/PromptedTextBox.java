package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This extends GWT's default {@link TextBox} to hint text when not focused
 */
public class PromptedTextBox extends TextBox implements KeyPressHandler, FocusHandler, BlurHandler, ClickHandler
{
	
	/** The prompt text. */
	private String promptText;
	
	/** The prompt style. */
	private String promptStyle;

	/**
	 * Instantiates a new prompted text box.
	 *
	 * @param promptText the prompt text
	 * @param promptStyleName the prompt style name
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #showPrompt()}
	 */
	public PromptedTextBox(String promptText, String promptStyleName)
	{
		this.promptText = promptText;
		this.promptStyle = promptStyleName;
		this.addKeyPressHandler(this);
		this.addFocusHandler(this);
		this.addClickHandler(this);
		this.addBlurHandler(this);
		showPrompt();
	}

	/**
	 * Show prompt.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public void showPrompt()
	{
		this.addStyleName(promptStyle);
		this.setText(this.promptText);
	}

	/**
	 * Hide prompt.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public void hidePrompt()
	{
		this.setText(null);
		this.removeStyleName(promptStyle);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
	 */
	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(promptText.equals(this.getText()) && !(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB))
		{
			hidePrompt();
		}
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event.dom.client.FocusEvent)
	 */
	@Override
	public void onFocus(FocusEvent event)
	{
		this.setCursorPos(0);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event)
	{
		if(promptText.equals(this.getText()))
			hidePrompt();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event.dom.client.BlurEvent)
	 */
	@Override
	public void onBlur(BlurEvent event)
	{
		if(getText().length() == 0)
			showPrompt();
	}
}