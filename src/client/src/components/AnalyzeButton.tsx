import classNames from '../utils/classNames'

import styles from '../css/AnalyzeButton.module.css'

export interface IAnalyzeButtonProps {
  id: string | number
  message: string
  onClick: (id: string | number) => void
  className?: string
  disabled?: boolean
  isAnalyzing: boolean | undefined
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
}

const AnalyzeButton = ({
  id,
  message,
  onClick,
  className,
  isAnalyzing,
  Icon,
}: IAnalyzeButtonProps) => {
  return (
    <button
      className={classNames(
        styles.analyze,
        className,
        isAnalyzing && styles.loading
      )}
      onClick={event => {
        event.stopPropagation()
        onClick(id)
      }}
    >
      {message}
      <Icon className={styles.icon} />
    </button>
  )
}

export default AnalyzeButton
