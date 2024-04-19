cd k8s_deployment


helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
helm repo add fluent https://fluent.github.io/helm-charts

cd kafka/
docker-compose up -d
kubectl apply -f service-kafka1.yaml
kubectl apply -f service-kafka2.yaml
kubectl apply -f service-kafka3.yaml
cd ..
cd pods
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard
kubectl apply -f deployment-cassandra.yaml 
kubectl apply -f deployment-front.yaml 
kubectl apply -f deployment-back.yaml 

kubectl apply -f service-account.yaml

cd ..
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard
helm upgrade --install my-fluent-bit fluent/fluent-bit -f .\fluent\values.yaml

kubectl  create token admin-user   
kubectl proxy    