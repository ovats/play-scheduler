name := """play-scheduler"""
//organization := "com.example"

version := "1.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
