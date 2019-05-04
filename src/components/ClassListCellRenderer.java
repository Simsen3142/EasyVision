package components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import database.ImageHandler;
import diagramming.view.FunctionView;
import functions.RepresentationIcon;

public class ClassListCellRenderer extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private volatile Map<Integer,ImageIcon> icons;
	private Map<Integer,String> texts;
	private List<JList<?>> lists;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object object, int index, boolean isSelected,
			boolean cellHasFocus) {
		if(lists==null)
			lists=new ArrayList<>();
		if(!lists.contains(list)) {
			lists.add(list);
		}
		
		index+=lists.indexOf(list)*1000;
		
		
		if(icons==null)
			icons=new HashMap<>();
		if(texts==null)
			texts=new HashMap<>();
		
		String text;
		ImageIcon ic=null;
		RepresentationIcon reprIc=null;
		
		if(texts.containsKey(index)) {
			text=texts.get(index);
			ic=icons.get(index);
		}else {
			if(object instanceof Class<?>) {
				Class<?>  clazz=(Class<?>)object;
				text=clazz.getSimpleName();
				
				try {
					Object o=clazz.getConstructor(Boolean.class).newInstance(true);
					if(o instanceof RepresentationIcon) {
						reprIc=((RepresentationIcon) o);
					}
				}catch (NoSuchMethodException e) {
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				text=object.toString();
			}
			texts.put(index, text);
		}
		
		boolean isLast=list.getModel().getSize()-1==index%1000;
		
		FunctionView label = new FunctionView(text,ic,index,isSelected,isLast);
		
		if(reprIc!=null) {
			final int i=index;
			reprIc.getRepresentationImage((img)->{
				ImageIcon icon=ImageHandler.getScaledImageIcon(img, 22, 20, Image.SCALE_AREA_AVERAGING);
				icons.put(i, icon);
				label.setIcon(icon);
				
				EventQueue.invokeLater(()->{
					list.revalidate();
					list.repaint();
				});
				
				return null;
			});
		}
		
		
		list.setPreferredSize(new Dimension(label.getWidth(), label.getPreferredSize().height*list.getModel().getSize()));

		
		if(isSelected) {
			for(JList<?> listX:lists) {
				if(!listX.equals(list)) {
					listX.clearSelection();
				}
			}
		}

		return label;
	}
}