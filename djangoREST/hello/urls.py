from django.conf.urls import url
from hello import views

urlpatterns = [
	url(r'by-name/(?P<name>[0-9a-zA-Z]+)/$', views.hello),
	url(r'by-surname/(?P<surname>[0-9a-zA-Z]+)/$', views.hello2),
]