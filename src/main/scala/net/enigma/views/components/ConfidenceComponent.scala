package net.enigma.views.components

import java.awt.event.FocusEvent

import scala.util.{Success, Try}

import com.vaadin.data.util.converter.StringToIntegerConverter
import com.vaadin.data.validator.IntegerRangeValidator
import com.vaadin.event.FieldEvents
import com.vaadin.ui.{Button, HorizontalLayout, Panel, TextField}
import org.vaadin.addons.rinne.events.ButtonClickListener

import net.enigma.Utils._
import net.enigma.{TextResources, ValueChangedListenable}

/**
 * @author Jacek Lewandowski
 */
class ConfidenceComponent extends Panel with ValueChangedListenable[Int] {

  val confidence = new TextField(TextResources.Labels.Confidence, "0")
  confidence.addFocusListener(new FieldEvents.FocusListener {
    override def focus(focusEvent: FieldEvents.FocusEvent): Unit = {
      confidence.selectAll()
    }
  })

  val confirmButton = new Button(TextResources.Labels.ConfirmConfidence)

  confirmButton.addClickListener(new ButtonClickListener(event ⇒ {
    Try(confidence.validate()) match {
      case Success(_) ⇒
        notifyListeners(confidence.getConvertedValue.asInstanceOf[Integer])
      case _ ⇒
    }
  }))

  confidence.setRequired(true)
  confidence.addValidator(new IntegerRangeValidator(TextResources.Notifications.ConfidenceValueInvalid, 51, 100))
  confidence.setConverter(new StringToIntegerConverter())
  confidence.setConversionError(TextResources.Notifications.ConfidenceValueInvalid)

  setContent(new HorizontalLayout(confidence, confirmButton).withSpacing.withMargins)

  def reset() = {
    confidence.setValue("0")
    confidence.setEnabled(false)
    confirmButton.setEnabled(false)
    setVisible(false)
  }

  def enable(): Unit = {
    confidence.setEnabled(true)
    confirmButton.setEnabled(true)
    setVisible(true)
  }

  def accepted(): Unit = {
    confidence.setEnabled(false)
    confirmButton.setEnabled(false)
  }

}
