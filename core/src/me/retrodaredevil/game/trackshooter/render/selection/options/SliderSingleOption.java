package me.retrodaredevil.game.trackshooter.render.selection.options;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Collection;

import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.options.ControlOption;
import me.retrodaredevil.controller.options.OptionValue;
import me.retrodaredevil.game.trackshooter.render.RenderObject;
import me.retrodaredevil.game.trackshooter.save.OptionSaver;

public class SliderSingleOption extends ControlOptionSingleOption {
	private static final float SLIDER_PERCENT_MULTIPLIER = .5f;
	private static final String STYLE_NAME = "small";
	private final RenderObject renderObject;

	private final Slider slider;
	private Label valueLabel = null;
	private Float sliderPercent = null;

	public SliderSingleOption(ControlOption controlOption, OptionSaver optionSaver, RenderObject renderObject){
		super(controlOption, optionSaver);
		this.renderObject = renderObject;

		OptionValue value = controlOption.getOptionValue();

		slider = new Slider((float) value.getMinOptionValue(), (float) value.getMaxOptionValue(),
				value.isOptionAnalog() ? .05f : 1, false, renderObject.getUISkin());
	}

	@Override
	protected void onInit(Table container){
		super.onInit(container);
		OptionValue value = controlOption.getOptionValue();

		slider.setValue((float) value.getOptionValue());
		valueLabel = new Label("", renderObject.getUISkin(), STYLE_NAME);

		container.add(valueLabel).width(60);
		container.add(slider).width(115);
		container.add().width(5);
		container.add(new Label("" + controlOption.getLabel(), renderObject.getUISkin(), STYLE_NAME)).width(220);
	}

	@Override
	public void onUpdate(Table table){
		super.onUpdate(table);
		valueLabel.setText(getNumberText(controlOption.getOptionValue().getOptionValue()));
	}

	@Override
	protected double getSetValue() {
		return slider.getValue();
	}

	private String getNumberText(double number){
		OptionValue value = controlOption.getOptionValue();
		if(value.isOptionAnalog()){
			return "" + ((int) Math.round(number * 100)) + "%";
		}
		return "" + ((int) Math.round(number));
	}
	@Override
	protected boolean canSave(){
		return !slider.isDragging();
	}

	@Override
	public void selectUpdate(float delta, JoystickPart selector, InputPart select, InputPart back, Collection<SelectAction> requestedActions) {
		fireInputEvents(slider, InputEvent.Type.enter);
		if(selector.getXAxis().isDown()) {
			if (sliderPercent == null) {
				sliderPercent = slider.getPercent();
			}
			sliderPercent += (float) selector.getX() * delta * SLIDER_PERCENT_MULTIPLIER;
			if (sliderPercent < 0) {
				sliderPercent = 0f;
			}
			if (sliderPercent > 1) {
				sliderPercent = 1f;
			}

			final float distance = slider.getMaxValue() - slider.getMinValue();
			slider.setValue(slider.getMinValue() + distance * sliderPercent);
		}
	}

	@Override
	public void deselect() {
		fireInputEvents(slider, InputEvent.Type.exit);
		sliderPercent = null;
	}
}
