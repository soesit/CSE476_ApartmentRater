from django.urls import path
from . import views

urlpatterns = [
    path('ping/', views.ping),
    path('apartments/', views.apartments, name='apartments'),
    path('apartments/<str:place_id>/', views.apartment_detail, name='apartment-detail'),
    path('apartments/<str:place_id>/reviews/', views.apartment_reviews, name='apartment-reviews'),
]
