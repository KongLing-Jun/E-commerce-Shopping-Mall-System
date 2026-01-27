pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Build') {
            steps {
                dir('backend-mall') {
                    sh 'mvn -B -DskipTests package'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('frontend-mall/frontend-mall') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker compose build'
            }
        }
    }
}
