import Banner from "../components/Home/Banner";
import Section from "../components/Home/Section";
import Highlight from "../components/Home/Highlight";
import TripleCardSection from '../components/YuYu/TripleCardSection';
import TopCarousel from "../components/YuYu/TopCarousel";
import BottomCarousel from "../components/YuYu/BottomCarousel";
import IcePopCardGrid from "../components/YuYu/IcePopCardGrid";

function Home() {

  const imagePaths = [
    "/images/062701.png",
    "/images/062702.png",
    "/images/062703.png",
    "/images/062704.png",
    "/images/062705.png",
    "/images/062706.png",
    "/images/062707.png",
    "/images/062708.png",
    "/images/062709.png",
    "/images/062710.png",
  ];

  return (
    <>
      <Banner />
      <Section />
      <Highlight />
      <TripleCardSection />
      <IcePopCardGrid />
      <TopCarousel images={imagePaths.slice(0, 5)} />
      <BottomCarousel images={imagePaths.slice(5)} />
    </>
  );
}
export default Home;