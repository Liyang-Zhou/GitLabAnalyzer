import { useContext, useEffect, useState } from 'react'
import dateConverter from '../utils/dateConverter'
import { UserConfigContext } from '../context/UserConfigContext'
import { ThemeProvider, Tooltip } from '@material-ui/core'
import tooltipTheme from '../themes/tooltipTheme'
import { TCommentData } from '../types'
import { LONG_COMMENT_LEN } from '../utils/constants'

import Table from '../components/Table'
import ExternalLink from '../components/ExternalLink'
import Dropdown from '../components/Dropdown'

import styles from '../css/CommentTable.module.css'

import warning from '../assets/warning.svg'

const isLongComment = (content: string) => content.length > LONG_COMMENT_LEN

const isInvalidUrl = (url: string) => url.includes('example')

const formatParentAuthor = (author: string) => {
  if (author === 'self') {
    return 'Self'
  } else if (author === '') {
    return 'Deleted user'
  }
  return author
}

export interface ICommentTableProps {
  comments: TCommentData
}

const CommentTable = ({ comments }: ICommentTableProps) => {
  const { userConfigs } = useContext(UserConfigContext)
  const [selectedRange, setSelectedRange] = useState(comments)

  useEffect(() => {
    const {
      startDate = new Date(Date.now() - 60 * 24 * 60 * 60 * 1000),
      endDate = new Date(),
    } = userConfigs.selected

    const range = comments.filter(
      comment =>
        new Date(comment.date).getTime() >= startDate.getTime() &&
        new Date(comment.date).getTime() <= endDate.getTime()
    )

    setSelectedRange(range?.length !== 0 ? range : comments?.slice(-1))
  }, [userConfigs.selected.startDate, userConfigs.selected.endDate, comments])

  return (
    <ThemeProvider theme={tooltipTheme}>
      <Table
        sortable
        headers={['Date', 'Comment', 'Word count', 'Type', 'By', 'GitLab link']}
        columnWidths={['1fr', '5fr', '0.8fr', '1fr', '0.8fr', '0.8fr']}
        classes={{
          container: styles.tableContainer,
          table: styles.table,
          header: styles.theader,
          data: styles.tdata,
        }}
        title={`Code review comments`}
        data={
          selectedRange?.map(
            ({ wordCount, content, date, context, webUrl, parentAuthor }) => {
              return {
                date: dateConverter(date, true),
                content: (
                  <div className={styles.commentContainer}>
                    <Dropdown
                      arrowOnLeft
                      fixedCollapsed={!isLongComment(content)}
                      className={styles.comment}
                      header={
                        <div className={styles.commentHeader}>{content}</div>
                      }
                    >
                      <div className={styles.commentBody}>{content}</div>
                    </Dropdown>
                  </div>
                ),
                wordCount,
                context: context === 'MergeRequest' ? 'Merge Request' : context,
                parentAuthor: formatParentAuthor(parentAuthor),
                gitlabUrl: isInvalidUrl(webUrl) ? (
                  <Tooltip
                    title="Unable to retrieve link due to server configuration error. "
                    placement="top"
                    arrow
                  >
                    <img className={styles.icon} src={warning} />
                  </Tooltip>
                ) : (
                  <ExternalLink link={webUrl} />
                ),
              }
            }
          ) ?? [{}]
        }
      />
    </ThemeProvider>
  )
}

export default CommentTable
