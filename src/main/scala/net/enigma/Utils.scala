package net.enigma

import java.util.UUID

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.Try

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.data.{Property, Validator}
import com.vaadin.event.FieldEvents.{BlurEvent, BlurListener, FocusEvent, FocusListener}
import com.vaadin.event.MouseEvents
import com.vaadin.server.Page
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import com.vaadin.ui._

/**
 * @author Jacek Lewandowski
 */
object Utils {

  implicit class RichAbstractComponent[T <: AbstractComponent](val component: T) extends AnyVal {
    def withHeight(height: String): T = {
      component.setHeight(height)
      component
    }

    def withWidth(width: String): T = {
      component.setWidth(width)
      component
    }

    def withSizeUndefined: T = {
      component.setSizeUndefined()
      component
    }

    def withSizeFull: T = {
      component.setSizeFull()
      component
    }

    def withFullWidth: T = {
      component.setWidth("100%")
      component
    }

    def withFullHeight: T = {
      component.setHeight("100%")
      component
    }

    def withAdditionalStyleName(styleName: String): T = {
      component.addStyleName(styleName)
      component
    }

    def withRemovedStyleName(styleName: String): T = {
      component.removeStyleName(styleName)
      component
    }

    def withData(data: AnyRef): T = {
      component.setData(data)
      component
    }

    def withImmediate: T = {
      component.setImmediate(true)
      component
    }

    def withAttribute(name: String, value: String): T = {
      if (component.getId == null) component.setId(UUID.randomUUID().toString)
      Try(Page.getCurrent.getJavaScript.execute(s"document.getElementById('${component.getId}').$name='$value'"))
      component
    }
  }

  implicit class RichAbstractOrderedLayout[T <: AbstractOrderedLayout](val layout: T) extends AnyVal {
    def withMargins: T = {
      layout.setMargin(true)
      layout
    }

    def withVerticalMargins: T = {
      layout.setMargin(new MarginInfo(false, true, false, true))
      layout
    }

    def withSpacing: T = {
      layout.setSpacing(true)
      layout
    }

    def withExpandRatio(component: Component, ratio: Float): T = {
      layout.setExpandRatio(component, ratio)
      layout
    }

    def withComponentAlignment(component: Component, alignment: Alignment): T = {
      layout.setComponentAlignment(component, alignment)
      layout
    }
  }

  implicit class RichLabel[T <: Label](val label: T) extends AnyVal {
    def withContentMode(contentMode: ContentMode): T = {
      label.setContentMode(contentMode)
      label
    }
  }

  implicit class RichAbstractField[T <: AbstractField[_]](val field: T) extends AnyVal {
    def withAdditionalValidator(validator: Validator): T = {
      field.addValidator(validator)
      field
    }

    def withConverter[V: ClassTag]: T = {
      val dataModelType = implicitly[ClassTag[V]].runtimeClass
      field.setConverter(dataModelType)
      field
    }

    def withConversionError(conversionError: String): T = {
      field.setConversionError(conversionError)
      field
    }

    def withConvertedValue(convertedValue: Any): T = {
      field.setConvertedValue(convertedValue)
      field
    }

    def convertedValue[R]: R = {
      field.getConvertedValue.asInstanceOf[R]
    }
  }

  implicit def toButtonClickListener(f: Button.ClickEvent ⇒ Any): Button.ClickListener = {
    new ClickListener {
      override def buttonClick(clickEvent: ClickEvent): Unit = f(clickEvent)
    }
  }

  implicit class RichButton[T <: Button](val field: T) extends AnyVal {
    def withClickListener(f: Button.ClickEvent => Any): T = {
      field.addClickListener(f)
      field
    }
  }

  implicit def toPropertyValueChangeListener(f: Property.ValueChangeEvent ⇒ Any): Property.ValueChangeListener = {
    new ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = f(event)
    }
  }

  implicit class RichAbstractTextField[T <: AbstractTextField](val field: T) extends AnyVal {
    def withMaxLength(maxLength: Int): T = {
      field.setMaxLength(maxLength)
      field
    }

    def withFocusListener(focusListener: FocusEvent ⇒ Unit): T = {
      field.addFocusListener(new FocusListener {
        override def focus(focusEvent: FocusEvent): Unit = focusListener(focusEvent)
      })
      field
    }

    def withBlurListener(blurListener: BlurEvent ⇒ Unit): T = {
      field.addBlurListener(new BlurListener {
        override def blur(blurEvent: BlurEvent): Unit = blurListener(blurEvent)
      })
      field
    }
  }

  implicit class RichPanel(val panel: Panel) extends AnyVal {
    def withClickListener(clickListener: MouseEvents.ClickEvent ⇒ Unit): Panel = {
      panel.addClickListener(new MouseEvents.ClickListener {
        override def click(clickEvent: MouseEvents.ClickEvent): Unit = clickListener(clickEvent)
      })
      panel
    }
  }

}
