package com.stan.person.view;

import com.stan.person.model.Investment;
import static com.stan.person.utility.Math.setPrecision;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import java.lang.Double;



@SuppressWarnings("hiding")
public class ColorCodedTableCellFactory<Investment, V> implements Callback<TableColumn<Investment, V>, TableCell<Investment, V>> {


    @Override
    public TableCell<Investment, V> call(TableColumn<Investment, V> p) {


        return new TableCell<Investment, V>() {

            @Override
            public void updateItem(Object item, boolean empty) {

                    if (empty) {
                        setText(null);

                    } else {

                    	if (item instanceof Double){

                    		if ((((java.lang.Double) item).compareTo(0.0) == 0) || (((java.lang.Double) item).compareTo(1.0) == 0 )) {
                    			setText(null);

                    		} else {
                    			setTextFill((((java.lang.Double) item).compareTo(0.0) > 0) ? Color.GREEN : Color.RED);
                    			//setStyle("-fx-background-color: white");
                    			setStyle("-fx-font-weight: normal");
                    			setText(setPrecision((java.lang.Double) item, 2).toString());
                    		}
                    	} else if (item instanceof String) {
                            String value = (String) item;

                            if (value.startsWith("-")) {
                                setTextFill(Color.RED);
                                
                            } else {
                            	setTextFill(Color.GREEN); 
                            }
                            setStyle("-fx-font-weight: normal");
                            setText(value);
                        }

                    }
                }


    };

}
}