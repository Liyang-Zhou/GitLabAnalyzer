import { ChangeEvent, useContext, useState } from 'react'
import { ThemeProvider, Tooltip } from '@material-ui/core'
import tooltipTheme from '../themes/tooltipTheme'
import classNames from '../utils/classNames'
import {
  TScoreType,
  IFileTypeScoring,
  UserConfigContext,
} from '../context/UserConfigContext'

import Selector from './Selector'
import Modal from './Modal'
import Table from './Table'

import { ReactComponent as Delete } from '../assets/delete.svg'
import { ReactComponent as Warning } from '../assets/error-small.svg'
import { ReactComponent as Add } from '../assets/add-file.svg'
import { ReactComponent as Save } from '../assets/save-large.svg'
import { ReactComponent as Reload } from '../assets/reload.svg'

import styles from '../css/UserConfigPopup.module.css'
import {
  ProjectContext,
  UPDATE_GENERAL_WEIGHT,
} from '../context/ProjectContext'

interface IUserConfigPopup {
  togglePopup: () => void
}

const UserConfigPopup = ({ togglePopup }: IUserConfigPopup) => {
  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const { dispatch: projectDispatch } = useContext(ProjectContext)
  const [fileScores, setFileScores] = useState([
    ...userConfigs.selected.fileScores,
  ])
  const [generalScores, setGeneralScores] = useState([
    ...userConfigs.selected.generalScores,
  ])
  const [newFileTypeName, setNewFileTypeName] = useState('')
  const [requireReanalyze, setRequireReanalyze] = useState(false)

  const generalScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    generalScores[index] = {
      type: generalScores[index].type,
      value: +event.target.value,
    }

    setGeneralScores([...generalScores])
  }

  const fileScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const field = event.target.name
    fileScores[index] = {
      ...fileScores[index],
      [field]:
        field === 'scoreMultiplier' ? +event.target.value : event.target.value,
    }

    setFileScores([...fileScores])

    if (field !== 'scoreMultiplier') {
      setRequireReanalyze(true)
    }
  }

  const deleteFileType = (index: number) => {
    fileScores.splice(index, 1)
    setFileScores([...fileScores])
    setRequireReanalyze(true)
  }

  const isValidFileType = () => {
    if (
      newFileTypeName === '' ||
      fileScores.map(score => score.fileExtension).includes(newFileTypeName)
    ) {
      return false
    } else {
      return true
    }
  }

  const addFileExtension = () => {
    const newFileType: IFileTypeScoring = {
      fileExtension: newFileTypeName,
      singleLineComment: '',
      multiLineCommentStart: '',
      multiLineCommentEnd: '',
      syntaxInCode: '',
      scoreMultiplier: 1,
    }
    fileScores.push(newFileType)
    setFileScores([...fileScores])
    setNewFileTypeName('')
    setRequireReanalyze(true)
  }

  const save = () => {
    dispatch({
      type: 'SET_SCORES',
      scores: { generalScores: generalScores, fileScores: fileScores },
    })

    projectDispatch({
      type: UPDATE_GENERAL_WEIGHT,
      weights: generalScores,
    })

    if (requireReanalyze) {
      // TODO: if requireReanalyze, redirect to home and start reanalyze
      setRequireReanalyze(false)
    }
  }

  const reset = () => {
    setGeneralScores([...userConfigs.selected.generalScores])
    setFileScores([...userConfigs.selected.fileScores])
  }

  const typeToString = (type: TScoreType) => {
    switch (type) {
      case 'ADD_FACTOR': {
        return 'New line of code'
      }
      case 'DELETE_FACTOR': {
        return 'Deleting a line'
      }
      case 'SYNTAX_FACTOR': {
        return 'Syntax only'
      }
      case 'BLANK_FACTOR': {
        return 'Comment/blank'
      }
      case 'SPACING_FACTOR': {
        return 'Spacing change'
      }
      default: {
        return ''
      }
    }
  }

  return (
    <Modal close={togglePopup}>
      <div className={styles.container}>
        <div className={styles.header}>Edit Scoring</div>
        <Selector tabHeaders={['Code Change Weights', 'File Type Weights']}>
          <div className={styles.selectorContainer}>
            <Table
              excludeHeaders
              columnWidths={['6fr', '2.2fr', '1fr']}
              classes={{ data: styles.row, table: styles.table }}
              data={generalScores.map((score, i) => {
                return {
                  type: typeToString(score.type),
                  input: (
                    <input
                      type="number"
                      step="0.2"
                      value={score.value}
                      className={classNames(
                        styles.generalInput,
                        styles.mediumInput
                      )}
                      onChange={e => generalScoresChange(e, i)}
                    />
                  ),
                  units: 'pts',
                }
              })}
            />
          </div>
          <div className={styles.selectorContainer}>
            <div className={styles.scrollContainer}>
              <Table
                headers={[
                  'File Extension',
                  'Single Line Comment',
                  'Multiline Comment Start',
                  'Multiline Comment End',
                  'Syntax Characters',
                  'Weights',
                  '',
                ]}
                columnWidths={[
                  '2fr',
                  '2fr',
                  '2fr',
                  '2fr',
                  '3.5fr',
                  '1fr',
                  '1fr',
                ]}
                classes={{ data: styles.skinnyRow, table: styles.table }}
                data={fileScores.map((score, i) => {
                  return {
                    type: score.fileExtension,
                    comment: (
                      <input
                        name="singleLineComment"
                        type="text"
                        value={score.singleLineComment}
                        className={classNames(
                          styles.generalInput,
                          styles.mediumInput
                        )}
                        onChange={e => fileScoresChange(e, i)}
                      />
                    ),
                    commentStart: (
                      <input
                        name="multiLineCommentStart"
                        type="text"
                        value={score.multiLineCommentStart}
                        className={classNames(
                          styles.generalInput,
                          styles.mediumInput
                        )}
                        onChange={e => fileScoresChange(e, i)}
                      />
                    ),
                    commentEnd: (
                      <input
                        name="multiLineCommentEnd"
                        type="text"
                        value={score.multiLineCommentEnd}
                        className={classNames(
                          styles.generalInput,
                          styles.mediumInput
                        )}
                        onChange={e => fileScoresChange(e, i)}
                      />
                    ),
                    syntax: (
                      <input
                        name="syntaxInCode"
                        type="text"
                        value={score.syntaxInCode}
                        className={classNames(
                          styles.generalInput,
                          styles.longInput
                        )}
                        placeholder="e.g. ,[]{}()=><?"
                        onChange={e => fileScoresChange(e, i)}
                      />
                    ),
                    weights: (
                      <input
                        name="scoreMultiplier"
                        type="number"
                        step="0.2"
                        value={score.scoreMultiplier}
                        className={classNames(
                          styles.generalInput,
                          styles.shortInput
                        )}
                        onChange={e => fileScoresChange(e, i)}
                      />
                    ),
                    end: (
                      <div className={styles.end}>
                        pts
                        <button
                          className={styles.delete}
                          onClick={() => deleteFileType(i)}
                        >
                          <Delete />
                        </button>
                      </div>
                    ),
                  }
                })}
              />
            </div>
            <div className={styles.addContainer}>
              <input
                type="text"
                placeholder="New File Extension..."
                className={styles.fileInput}
                value={newFileTypeName}
                onChange={e => setNewFileTypeName(e.target.value)}
              />
              <button
                onClick={addFileExtension}
                className={styles.addButton}
                disabled={!isValidFileType()}
              >
                <Add className={styles.addIcon} />
                Add file type
              </button>
            </div>
          </div>
        </Selector>
        <div className={styles.saveContainer}>
          <button onClick={save} className={styles.saveButton}>
            <Save className={styles.saveIcon} />
            Save score settings
          </button>
          <ThemeProvider theme={tooltipTheme}>
            <Tooltip title={'Reset Changes'} arrow>
              <button onClick={reset}>
                <Reload className={styles.resetButton} />
              </button>
            </Tooltip>
          </ThemeProvider>
        </div>
        {requireReanalyze && (
          <div className={styles.warningContainer}>
            <Warning className={styles.warningIcon} /> These changes will not be
            applied until projects are re-analyzed
          </div>
        )}
      </div>
    </Modal>
  )
}

export default UserConfigPopup
