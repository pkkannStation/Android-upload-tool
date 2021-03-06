package org.controlsfx.property.editor;

import java.math.BigInteger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

/*
 * TODO replace this with proper API when it becomes available:
 * https://javafx-jira.kenai.com/browse/RT-30881
 */
class NumericField extends TextField {

    private final NumericValidator<? extends Number> value ;
    		
    public NumericField( Class<? extends Number> cls ) {
    	
    	if ( cls == byte.class || cls == Byte.class || cls == short.class || cls == Short.class ||
    		 cls ==	int.class  || cls == Integer.class || cls == long.class || cls == Long.class ||
    	     cls == BigInteger.class) {
    		value = new LongValidator(this);
    	} else {
    		value = new DoubleValidator(this);
    	}
    	
        textProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable arg0) {
                value.setValue(value.toNumber(getText()));
            }
        });
        
    }
    
    public final ObservableValue<Number> valueProperty() {
        return value;
    }

    @Override public void replaceText(int start, int end, String text) {
        if (replaceValid(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override public void replaceSelection(String text) {
        IndexRange range = getSelection();
        if (replaceValid(range.getStart(), range.getEnd(), text)) {
            super.replaceSelection(text);
        }
    }

    private Boolean replaceValid(int start, int end, String fragment) {
        try {
        	String newText = getText().substring(0, start) + fragment + getText().substring(end);
        	if (newText.isEmpty()) return true; 
			value.toNumber(newText);
        	return true;
        } catch( Throwable ex ) {
        	return false;
        }
    }
    
    
    private static abstract interface NumericValidator<T extends Number> extends NumberExpression {
    	void setValue(Number num);
    	T toNumber(String s);
    	
    }
    
    static class DoubleValidator extends SimpleDoubleProperty implements NumericValidator<Double>{
    	
    	private NumericField field;
    	
    	public DoubleValidator(NumericField field) {
    		super(field, "value", 0.0); //$NON-NLS-1$
    		this.field = field;
		}
    	
    	@Override protected void invalidated() {
            field.setText(Double.toString(get()));
        }

		@Override
		public Double toNumber(String s) {
			if ( s == null || s.trim().isEmpty() ) return 0d;
	    	String d = s.trim();
	    	if ( d.endsWith("f") || d.endsWith("d") || d.endsWith("F") || d.endsWith("D") ) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	    		throw new NumberFormatException("There should be no alpha symbols"); //$NON-NLS-1$
	    	}
	    	return new Double(d);
		};
		
    }
 
    
    static class LongValidator extends SimpleLongProperty implements NumericValidator<Long>{
    	
    	private NumericField field;
    	
    	public LongValidator(NumericField field) {
    		super(field, "value", 0l); //$NON-NLS-1$
    		this.field = field;
		}
    	
    	@Override protected void invalidated() {
            field.setText(Long.toString(get()));
        }

		@Override
		public Long toNumber(String s) {
			if ( s == null || s.trim().isEmpty() ) return 0l;
	    	String d = s.trim();
	    	return new Long(d);
		};
		
    }    
    
    
}