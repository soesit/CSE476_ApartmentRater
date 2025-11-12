from django.urls import path
from . import views


urlpatterns = [
    path('ping/', views.ping),
    path('apartments/', views.apartment_list, name='apartment-list'),
]
