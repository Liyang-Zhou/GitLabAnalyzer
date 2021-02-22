import ProjectStat from '../components/ProjectStat'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/ProjectSummary.module.css'
import { IProjectData } from '../pages/Project'
import { useState } from 'react'

const calcAgeInDays = (birth: number) => {
  const diff = Date.now() - birth
  return diff / (24 * 60 * 60 * 1000)
}

const bytesToMb = (bytes: number) => bytes / (1024 * 1024)

const ProjectSummary = ({ project }: { project: IProjectData | undefined }) => {
  const [yAxis, setYAxis] = useState<'number' | 'score'>('number')

  if (!project) return null

  const { id, members, numBranches, numCommits, createdAt, repoSize } = project

  return (
    <div className={styles.container}>
      <div className={styles.config}>
        <p className={styles.configHeader}>CONFIGURE</p>
        <p className={styles.category}>Y-Axis:</p>
        <div className={styles.option}>
          <input
            type="radio"
            value="number"
            name="yaxis"
            onChange={() => setYAxis('number')}
            defaultChecked
          />
          Number
        </div>
        <div className={styles.option}>
          <input
            type="radio"
            value="score"
            name="yaxis"
            onChange={() => setYAxis('score')}
          />
          Points
        </div>
      </div>
      <div className={styles.statsContainer}>
        <div className={styles.graph}>
          <ActivityGraph
            mergeUrl={`/api/project/${id}/mergerequests`}
            commitUrl={`/api/project/${id}/commits`}
            yAxisValue={yAxis}
          />
        </div>
        <div className={styles.stats}>
          <ProjectStat name="Members" value={members.length} />
          <ProjectStat name="Branches" value={numBranches} />
          <ProjectStat name="Commits" value={numCommits} />
          <ProjectStat
            name="Average commits per day"
            value={(numCommits / calcAgeInDays(createdAt)).toFixed(2)}
          />
          <ProjectStat
            name="Files"
            value={`${bytesToMb(repoSize).toFixed(2)} MB`}
          />
        </div>
      </div>
    </div>
  )
}

export default ProjectSummary
