package net.enigma.presenter

import java.io.{FileInputStream, InputStream}

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.{FileDownloader, StreamResource}
import com.vaadin.server.StreamResource.StreamSource
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

import net.enigma.App.Views.Thanks
import net.enigma.{App, views}
import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait AdminPresenter extends FlowPresenter {
  self: views.AdminView ⇒

  private val logger = LoggerFactory.getLogger(classOf[AdminPresenter])

  def populateTable(): Unit = {
    summaryTable.removeAllItems()
    val users = App.service.getAllUsers()
    val summaries = for ((ctg, ctgUsers) <- users.groupBy(_.category))
      yield (ctg, for ((stage, stageUsers) <- ctgUsers.groupBy(_.currentStage))
        yield (stage, stageUsers.length))

    for ((group, stagesMap) <- summaries) {
      val finished = stagesMap.getOrElse(Some(Thanks.name), 0)
      val notStarted = stagesMap.getOrElse(None, 0)
      val started = stagesMap.values.sum - finished - notStarted
      val row = Array[AnyRef](group, notStarted: Integer, started: Integer, finished: Integer)
      summaryTable.addItem(row, group)
    }
  }

  override def entered(event: ViewChangeEvent): Unit = {
    populateTable()
    generatedCodesTextArea.setValue("")
  }

  override def accept(): Boolean = false

  generateGroupButton.withClickListener(event => {
    if (StringUtils.isNotBlank(groupNameField.getValue)) {
      val userId = App.service.generateNewGroup(groupNameField.getValue)
      generatedCodesTextArea.setValue(generatedCodesTextArea.getValue + s"\n${App.LINK}$userId\n")
      populateTable()
    }

  })

  generateUserButton.withClickListener(event => {
    if (StringUtils.isNotBlank(groupNameField.getValue)) {
      val groupId = App.service.generateNewUser(groupNameField.getValue)
      generatedCodesTextArea.setValue(generatedCodesTextArea.getValue + s"\n${App.LINK}$groupId\n")
      populateTable()
    }
  })

  val resource = new StreamResource(new StreamSource {
    override def getStream: InputStream = {
      val backupPath = App.backup()
      new FileInputStream(backupPath.toFile)
    }
  }, "experiment-data.zip")

  val fileDownloader = new FileDownloader(resource)
  fileDownloader.extend(backupButton)

}
