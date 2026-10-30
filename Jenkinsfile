// ============================================================
// Weblog CI/CD 流水线（Jenkins Declarative Pipeline）
// 入口：仓库根 Jenkinsfile（Jenkins 新建 Pipeline 任务指向本仓库）
// 流程：Checkout → 后端 mvn test → 前端 npm ci+lint+build
//       → docker build 双镜像 → 登录 TCR 推送 → SSH 部署 → 失败回滚
// 前置 Jenkins 配置（Manage Jenkins）：
//   1) 凭据：
//      - 类型 Username with password，ID = tcr-credentials（TCR 用户名/密码）
//      - 类型 SSH Username with private key，ID = deploy-ssh-key（目标服务器私钥）
//   2) 插件：Pipeline、Git、Credentials Binding、SSH Agent、Docker Pipeline
//   3) 构建参数见下方 parameters（NAMESPACE / 部署目标）
// ============================================================

pipeline {
    agent any

    parameters {
        // TCR 命名空间（需先在腾讯云 TCR 控制台创建）
        string(name: 'NAMESPACE', defaultValue: 'weblog', description: '腾讯云 TCR 命名空间')
        // 部署目标服务器（同机部署默认本机 IP，也可改独立节点）
        string(name: 'DEPLOY_HOST', defaultValue: '62.234.30.116', description: '部署目标服务器地址')
        string(name: 'DEPLOY_USER', defaultValue: 'ubuntu', description: '部署目标服务器 SSH 用户')
        string(name: 'DEPLOY_DIR', defaultValue: '/opt/weblog', description: '服务器上的仓库目录（含 deploy/scripts）')
    }

    environment {
        // 镜像仓库（腾讯云 TCR 个人版）
        REGISTRY = 'ccr.ccs.tencentcloud.com'
        // 镜像 tag：build-${BUILD_NUMBER}，与 TCR 推送、deploy.sh 保持一致
        IMAGE_TAG = "build-${env.BUILD_NUMBER}"
        SERVER_IMAGE = "${REGISTRY}/${params.NAMESPACE}/weblog-server:${IMAGE_TAG}"
        WEB_IMAGE = "${REGISTRY}/${params.NAMESPACE}/weblog-web:${IMAGE_TAG}"
        // SSH 通用参数（StrictHostKeyChecking=no 首次连接免确认）
        SSH_OPTS = "-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
    }

    stages {
        // ----------------------------------------------------------
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ----------------------------------------------------------
        // 后端：Java 8 单元测试（复用 backend.Dockerfile 的 builder 阶段）
        // 说明：采用 `docker build`（上下文由 docker CLI 流式传输到宿主 daemon），
        //       避免 DooD 模式下 `docker run -v $WORKSPACE` 宿主路径不可解析的问题。
        // ----------------------------------------------------------
        stage('后端测试') {
            steps {
                sh "docker build -f deploy/backend.Dockerfile --target builder --build-arg SKIP_TESTS=false -t weblog-server:test ."
            }
        }

        // ----------------------------------------------------------
        // 前端：npm ci + lint + build（复用 frontend.Dockerfile 的 builder 阶段）
        // ----------------------------------------------------------
        stage('前端构建') {
            steps {
                sh "docker build -f deploy/frontend.Dockerfile --target builder --build-arg RUN_LINT=true -t weblog-web:test ."
            }
        }

        // ----------------------------------------------------------
        // 构建双镜像（tag = build-${BUILD_NUMBER}）
        // ----------------------------------------------------------
        stage('构建镜像') {
            steps {
                sh "docker build -f deploy/backend.Dockerfile -t ${SERVER_IMAGE} ."
                sh "docker build -f deploy/frontend.Dockerfile -t ${WEB_IMAGE} ."
            }
        }

        // ----------------------------------------------------------
        // 登录 TCR 并推送镜像
        // ----------------------------------------------------------
        stage('推送镜像') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: 'tcr-credentials',
                        usernameVariable: 'TCR_USERNAME',
                        passwordVariable: 'TCR_PASSWORD')]) {
                    sh '''
                        echo "${TCR_PASSWORD}" | docker login "${REGISTRY}" -u "${TCR_USERNAME}" --password-stdin
                    '''
                    sh "docker push ${SERVER_IMAGE}"
                    sh "docker push ${WEB_IMAGE}"
                }
            }
        }

        // ----------------------------------------------------------
        // SSH 执行服务端 deploy.sh（pull + 滚动更新 + 健康检查 + 失败自动回滚）
        // ----------------------------------------------------------
        stage('部署') {
            steps {
                sshagent(['deploy-ssh-key']) {
                    sh """
                        ssh ${SSH_OPTS} ${params.DEPLOY_USER}@${params.DEPLOY_HOST} \
                            "cd ${params.DEPLOY_DIR} && IMAGE_TAG=${IMAGE_TAG} bash ./deploy/scripts/deploy.sh"
                    """
                }
            }
        }

        // ----------------------------------------------------------
        // 部署后冒烟测试（服务端 smoke-test.sh）
        // ----------------------------------------------------------
        stage('冒烟测试') {
            steps {
                sshagent(['deploy-ssh-key']) {
                    sh """
                        ssh ${SSH_OPTS} ${params.DEPLOY_USER}@${params.DEPLOY_HOST} \
                            "cd ${params.DEPLOY_DIR} && BASE_URL=http://localhost:80 bash ./deploy/scripts/smoke-test.sh"
                    """
                }
            }
        }
    }

    post {
        // ----------------------------------------------------------
        // 失败兜底回滚：deploy.sh 内部已处理健康检查失败回滚，
        // 此处作为部署阶段失败（如 SSH 中断、脚本报错）的二次保障。
        // ----------------------------------------------------------
        failure {
            script {
                if (currentBuild.result == 'FAILURE') {
                    sshagent(['deploy-ssh-key']) {
                        sh """
                            ssh ${SSH_OPTS} ${params.DEPLOY_USER}@${params.DEPLOY_HOST} \
                                "cd ${params.DEPLOY_DIR} && bash ./deploy/scripts/rollback.sh || true"
                        """
                    }
                }
            }
        }
        success {
            echo ">> 流水线成功：${SERVER_IMAGE} / ${WEB_IMAGE} 已部署"
        }
        always {
            // 清理本次构建临时 tag 之外的悬空镜像（保留最近 3 次，控制磁盘）
            sh 'docker image prune -f --filter "until=24h" || true'
        }
    }
}
