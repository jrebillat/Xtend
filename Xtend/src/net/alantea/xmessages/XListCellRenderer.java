package net.alantea.xmessages;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

@SuppressWarnings("serial")
public class XListCellRenderer extends DefaultListCellRenderer
{
   @Override
   public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
         boolean cellHasFocus)
   {
      return super.getListCellRendererComponent(list, XMessages.get((value == null) ? " " : value.toString()), index, isSelected, cellHasFocus);
   }
}
