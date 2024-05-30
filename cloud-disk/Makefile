default:
	docker build -t cloud-disk .
	docker run --name cloud-disk -p 8080:8080 -d cloud-disk

clean:
	docker stop cloud-disk
	docker rm cloud-disk
	docker rmi cloud-disk
