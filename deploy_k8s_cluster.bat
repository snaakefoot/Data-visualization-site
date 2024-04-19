cd k8s_deployment


helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/

cd pods
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard
kubectl apply -f deployment-cassandra.yaml 
kubectl apply -f deployment-front.yaml 
kubectl apply -f deployment-back.yaml 

kubectl apply -f service-account.yaml

cd ..
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard

kubectl  port-forward svc/kubernetes-dashboard-kong-proxy 8443:443
kubectl  create  token admin-user   
 
